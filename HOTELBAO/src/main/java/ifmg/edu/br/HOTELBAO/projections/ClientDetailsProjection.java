package ifmg.edu.br.HOTELBAO.projections;

public interface ClientDetailsProjection {
    String getClientEmail();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
