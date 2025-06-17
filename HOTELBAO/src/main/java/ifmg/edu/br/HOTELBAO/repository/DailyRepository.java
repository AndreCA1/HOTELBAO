package ifmg.edu.br.HOTELBAO.repository;

import ifmg.edu.br.HOTELBAO.entities.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {
}
