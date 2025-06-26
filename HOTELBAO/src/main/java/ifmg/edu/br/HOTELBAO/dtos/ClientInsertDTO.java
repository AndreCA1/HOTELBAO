package ifmg.edu.br.HOTELBAO.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientInsertDTO extends ClientDTO{
    @NotBlank
    @Size(min = 6, max = 100, message = "Deve ter entre 6 e 100 caracteres")
    private String password;

    public ClientInsertDTO () { super(); }

    public ClientInsertDTO(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
