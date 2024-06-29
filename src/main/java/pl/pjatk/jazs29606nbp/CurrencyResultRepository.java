package pl.pjatk.jazs29606nbp;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.jazs29606nbp.model.Result;

public interface CurrencyResultRepository extends JpaRepository<Result, Long> {
    
}
