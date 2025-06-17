package ifmg.edu.br.HOTELBAO.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean active;
    @Column(columnDefinition = "TEXT")
    private String description;
    private float price;
    private String imageUrl;

    public Room() {
        this.active = true;
    }

    public Room(long id, String description, float price, String imageUrl) {
        this.id = id;
        this.active = true;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    //get and set
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getActive() {return active;}

    public void setActive(boolean active) {this.active = active;}

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
        if (!(o instanceof Room room)) return false;
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
                ", active=" + active +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
