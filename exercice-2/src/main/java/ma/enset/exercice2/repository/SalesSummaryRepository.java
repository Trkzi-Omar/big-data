package ma.enset.exercice2.repository;

import ma.enset.exercice2.model.SalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Long> {
} 