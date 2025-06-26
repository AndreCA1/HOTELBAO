package ifmg.edu.br.HOTELBAO.repository;

import ifmg.edu.br.HOTELBAO.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {

    @Query("SELECT objeto FROM PasswordRecover objeto WHERE (objeto.token = :token) AND (objeto.expiration > :now)")
    public PasswordRecover searchValidToken(String token, Instant now);
}
