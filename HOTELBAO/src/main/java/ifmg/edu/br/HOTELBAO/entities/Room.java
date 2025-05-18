package ifmg.edu.br.HOTELBAO.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "TEXT")
    private String description;
    private float value;
    private String imageUrl;

    public Room() {}

    public Room(long id, String description, float value, String imageUrl) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.imageUrl = imageUrl;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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
                ", description='" + description + '\'' +
                ", value=" + value +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
