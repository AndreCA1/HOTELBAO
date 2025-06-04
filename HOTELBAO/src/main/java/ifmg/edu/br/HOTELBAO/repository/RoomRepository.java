package ifmg.edu.br.HOTELBAO.repository;

import ifmg.edu.br.HOTELBAO.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
