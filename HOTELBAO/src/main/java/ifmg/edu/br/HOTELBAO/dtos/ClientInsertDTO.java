package ifmg.edu.br.HOTELBAO.dtos;

public class ClientInsertDTO extends ClientDTO{
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
