package ifmg.edu.br.HOTELBAO.dtos;

import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClientDTO {
    @Schema(description = "Database generated ID Client")
    private long id;

    @Schema(description = "Client name")
    @Size(min = 1, max = 100, message = "Deve ter entre 1 e 100 caracteres")
    private String name;

    @Schema(description = "Client email and username")
    @Email(message = "Favor informar Email válido")
    @NotBlank(message = "Email não pode ser vazio")
    private String email;

    @Schema(description = "Client phone number")
    @NotBlank(message = "Campo obrigatório")
    private String phone;

    Set<RoleDTO> roles = new HashSet<>();

    public ClientDTO() {
    }

    public ClientDTO(long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public ClientDTO(Client entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();

        entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }

    public void addRole(RoleDTO role) { roles.add(role); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClientDTO clientDTO)) return false;
        return id == clientDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
