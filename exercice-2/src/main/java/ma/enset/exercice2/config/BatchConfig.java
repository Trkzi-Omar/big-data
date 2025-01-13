package ma.enset.exercice2.config;

import ma.enset.exercice2.model.Sale;
import ma.enset.exercice2.model.SalesSummary;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableScheduling
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Sale> reader() {
        FlatFileItemReader<Sale> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("sales.csv"));
        reader.setLinesToSkip(1);
        reader.setStrict(false);

        DefaultLineMapper<Sale> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("transactionId", "userId", "productId", "timestamp", "amount", "category", "quantity", "unitPrice");

        BeanWrapperFieldSetMapper<Sale> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Sale.class);

        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(String.class, LocalDateTime.class, source -> 
            LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        fieldSetMapper.setConversionService(conversionService);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);

        return reader;
    }

    @Bean
    public SalesProcessor processor() {
        return new SalesProcessor();
    }

    @Bean
    public JpaItemWriter<SalesSummary> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<SalesSummary> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Job salesJob(JobRepository jobRepository, Step salesStep) {
        return new JobBuilder("salesJob", jobRepository)
                .start(salesStep)
                .build();
    }

    @Bean
    public Step salesStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager,
                         FlatFileItemReader<Sale> reader,
                         SalesProcessor processor,
                         JpaItemWriter<SalesSummary> writer) {
        return new StepBuilder("salesStep", jobRepository)
                .<Sale, SalesSummary>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
} 