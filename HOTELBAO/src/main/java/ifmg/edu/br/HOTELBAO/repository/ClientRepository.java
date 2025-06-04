package ifmg.edu.br.HOTELBAO.repository;

import ifmg.edu.br.HOTELBAO.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
