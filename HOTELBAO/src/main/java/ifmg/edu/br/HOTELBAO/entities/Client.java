package ifmg.edu.br.HOTELBAO.entities;

import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean active;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;


    public Client() {
        this.active = true;
    }

    public Client(long id, String name, String email, String password, String phone) {
        this.id = id;
        this.active = true;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public Client(ClientDTO dto) {
        this.id = dto.getId();
        this.active = true;
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    //end get and set

    //equals and hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Client client)) return false;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", active=" + active +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
