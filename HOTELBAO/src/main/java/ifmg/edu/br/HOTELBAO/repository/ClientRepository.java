package ifmg.edu.br.HOTELBAO.repository;

import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.projections.ClientDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);

    @Query(nativeQuery = true,
            value = """ 
                SELECT c.email as ClientEmail, c.password, r.id as RoleId, r.authority as Authority
                FROM client c 
                                INNER JOIN client_role cr 
                                                ON c.id = cr.client_id 
                                INNER JOIN role r 
                                                ON r.id = cr.role_id
                            WHERE c.email = :username
            """
        )
    List<ClientDetailsProjection> searchClientAndRoleByEmail(String username);
}
