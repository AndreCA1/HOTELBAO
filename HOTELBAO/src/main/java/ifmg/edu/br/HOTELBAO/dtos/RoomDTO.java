package ifmg.edu.br.HOTELBAO.dtos;

import ifmg.edu.br.HOTELBAO.entities.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

public class RoomDTO {
    @Schema(description = "Database generated ID Room")
    private long id;

    @Schema(description = "Room description")
    @NotBlank
    private String description;

    @Schema(description = "Room price per stay")
    @Positive(message = "Preço deve ter valor positivo")
    private float price;

    @Schema(description = "Room image URL")
    private String imageUrl;

    public RoomDTO() {}

    public RoomDTO(long id, String description, float price, String imageUrl) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public RoomDTO(Room entity) {
        this.id = entity.getId();
        this.description = entity.getDescription();
        this.price = entity.getPrice();
        this.imageUrl = entity.getImageUrl();
    }

    //get and set
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    //end get and set

    //equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomDTO room)) return false;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    //end equals and hashcode

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
