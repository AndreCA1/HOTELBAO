package ifmg.edu.br.HOTELBAO.dtos;

import ifmg.edu.br.HOTELBAO.entities.Daily;
import ifmg.edu.br.HOTELBAO.entities.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Objects;

public class DailyDTO extends RepresentationModel<DailyDTO> {
    @Schema(description = "Database generated ID Daily")
    private long id;

    @Schema(description = "Start date of stay")
    private Date dailyDate;

    private long clientId;
    private Room room;

    public DailyDTO() {
    }

    public DailyDTO(long id, Date dailyDate, long clientId, Room room) {
        this.id = id;
        this.dailyDate = dailyDate;
        this.clientId = clientId;
        this.room = room;
    }

    public DailyDTO(Daily entity) {
        this.id = entity.getId();
        this.dailyDate = entity.getDailyDate();
        this.clientId = entity.getClient().getId();
        this.room = entity.getRoom();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(Date dailyDate) {
        this.dailyDate = dailyDate;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DailyDTO dailyDTO)) return false;
        return Objects.equals(dailyDate, dailyDTO.dailyDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dailyDate);
    }

    @Override
    public String toString() {
        return "DailyDTO{" +
                "id=" + id +
                ", dailyDate=" + dailyDate +
                ", clientId=" + clientId +
                ", room=" + room +
                '}';
    }
}
