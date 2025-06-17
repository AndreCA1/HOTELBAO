package ifmg.edu.br.HOTELBAO.dtos;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.Objects;

public class DailyInsertDTO {
    @Schema(description = "Database generated ID Daily")
    private long id;
    @Schema(description = "Start date of stay")
    private Date dailyDate;
    @Schema(description = "Database generated ID Client")
    @NotNull(message = "Campo obrigatório")
    private long client;
    @Schema(description = "Database generated ID Room")
    @NotNull(message = "Campo obrigatório")
    private long room;

    public DailyInsertDTO() {
    }

    public DailyInsertDTO(long id, Date dailyDate, long client, long room) {
        this.id = id;
        this.dailyDate = dailyDate;
        this.client = client;
        this.room = room;
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

    public long getClient() {
        return client;
    }

    public void setClient(long client) {
        this.client = client;
    }

    public long getRoom() {
        return room;
    }

    public void setRoom(long room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DailyInsertDTO that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DailyInsertDTO{" +
                "id=" + id +
                ", dailyDate=" + dailyDate +
                ", client=" + client +
                ", room=" + room +
                '}';
    }
}
