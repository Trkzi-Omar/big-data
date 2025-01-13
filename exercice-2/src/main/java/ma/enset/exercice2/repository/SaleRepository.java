package ma.enset.exercice2.repository;

import ma.enset.exercice2.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
} 