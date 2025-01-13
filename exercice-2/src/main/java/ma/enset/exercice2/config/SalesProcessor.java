package ma.enset.exercice2.config;

import ma.enset.exercice2.model.Sale;
import ma.enset.exercice2.model.SalesSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;
import java.util.Map;

public class SalesProcessor implements ItemProcessor<Sale, SalesSummary> {
    private static final Logger log = LoggerFactory.getLogger(SalesProcessor.class);
    private Map<String, SalesSummary> summaryMap = new HashMap<>();

    @Override
    public SalesSummary process(Sale sale) {
        log.info("Processing sale: {}", sale);
        
        if (sale.getQuantity() == null || sale.getUnitPrice() == null || 
            sale.getQuantity() <= 0 || sale.getUnitPrice() <= 0) {
            log.warn("Skipping invalid record: {}", sale);
            return null;
        }

        String category = sale.getCategory();
        if (category == null || category.trim().isEmpty()) {
            log.warn("Skipping record with no category: {}", sale);
            return null;
        }

        double revenue = sale.getQuantity() * sale.getUnitPrice();
        log.info("Calculated revenue for category {}: {}", category, revenue);

        SalesSummary summary = summaryMap.computeIfAbsent(category, k -> {
            SalesSummary s = new SalesSummary();
            s.setCategory(category);
            s.setTotalRevenue(0.0);
            s.setTotalProducts(0);
            s.setAverageRevenuePerProduct(0.0);
            return s;
        });

        summary.setTotalRevenue(summary.getTotalRevenue() + revenue);
        summary.setTotalProducts(summary.getTotalProducts() + sale.getQuantity());
        summary.setAverageRevenuePerProduct(
                summary.getTotalRevenue() / summary.getTotalProducts()
        );

        log.info("Updated summary for category {}: {}", category, summary);
        return summary;
    }
} 