package ifmg.edu.br.HOTELBAO.util;

import ifmg.edu.br.HOTELBAO.dtos.*;

import java.time.Instant;
import java.util.Date;

public class Factory {
    public static ClientInsertDTO createClientInsertDTO(){
        ClientInsertDTO client = new ClientInsertDTO();
        client.setName("client");
        client.setEmail("email@email.com");
        client.setPassword("password");
        client.setPhone("12345678910");
        return client;
    }

    public static RoomDTO createRoomDTO(){
        RoomDTO room = new RoomDTO();
        room.setDescription("teste");
        room.setPrice(1000);
        room.setImageUrl("https://example.com/quarto1.jpg");
        return room;
    }

    public static DailyInsertDTO createDailyInsertDTO(Long clientId, Long roomId){
        DailyInsertDTO daily = new DailyInsertDTO();

        daily.setClient(clientId);
        daily.setRoom(roomId);
        daily.setDailyDate(Date.from(Instant.now()));
        return daily;
    }

    public static EmailDTO createEmailDTO(){
        EmailDTO email = new EmailDTO();
        email.setBody("a test mail from tests");
        email.setTo("nobex27770@exitbit.com");
        email.setSubject("Test email");
        return email;
    }

    public static RequestTokenDTO createRequestTokenDTO(String email){
        RequestTokenDTO token = new RequestTokenDTO();
        token.setEmail(email);
        return token;
    }

    public static NewPasswordDTO createNewPasswordDTO(String token){
        NewPasswordDTO newPassword = new NewPasswordDTO();
        newPassword.setToken(token);
        newPassword.setNewPassword("password");
        return newPassword;
    }
}
