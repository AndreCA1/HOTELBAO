package ifmg.edu.br.HOTELBAO.repository;

import ifmg.edu.br.HOTELBAO.entities.Daily;
import ifmg.edu.br.HOTELBAO.projections.DailyDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {
    @Query(nativeQuery = true,
            value = """ 
                SELECT EXISTS
                                (SELECT 1
                FROM daily d 
                            WHERE d.room_id = :room_id 
                                          AND DATE(d.daily_date) = DATE(:date)
                            )
            """
    )
    int searchDailyByRoomAndDate(Long room_id, Date date);

    @Query(nativeQuery = true,
            value = """
                        SELECT d.id AS id,
                              d.daily_date AS daily_date,
                              d.client_id AS client_id,
                              r.id AS room_id,
                              r.description AS room_description,
                              r.price AS room_price,
                              r.image_url AS room_image_url 
                                                FROM daily d INNER JOIN room r ON d.room_id = r.id WHERE d.client_id = :client_id;
            
                        """,
            countQuery = "SELECT COUNT(*) FROM daily WHERE client_id = :client_id"
    )
    Page<DailyDetailsProjection> searchDailyByClientId(Long client_id, Pageable pageable);
}
