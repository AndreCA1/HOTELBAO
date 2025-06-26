package ifmg.edu.br.HOTELBAO.projections;

import java.util.Date;

public interface DailyDetailsProjection {
    Long getId();
    Date getDaily_date();
    Long getClient_id();

    // Campos da Room
    Long getRoom_id();
    String getRoom_description();
    Float getRoom_price();
    String getRoom_image_url();
}
