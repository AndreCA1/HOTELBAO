package ifmg.edu.br.HOTELBAO.dtos;

import jakarta.validation.constraints.NotBlank;


public class NewPasswordDTO {
    @NotBlank(message = "Campo obrigatório")
    private String newPassword;
    @NotBlank(message = "Campo obrigatório")
    private String token;

    public NewPasswordDTO() {
    }

    public NewPasswordDTO(String newPassword, String token) {
        this.newPassword = newPassword;
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "NewPasswordDTO{" +
                "newPassword='" + newPassword + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
