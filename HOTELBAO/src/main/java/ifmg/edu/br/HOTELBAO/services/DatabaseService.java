package ifmg.edu.br.HOTELBAO.services;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private Flyway flyway;

    public void cleanAndMigrate() {
        flyway.clean();
        flyway.migrate();
    }
}
