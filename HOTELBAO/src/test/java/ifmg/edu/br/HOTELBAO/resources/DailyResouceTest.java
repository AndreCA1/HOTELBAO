package ifmg.edu.br.HOTELBAO.resources;


import com.fasterxml.jackson.databind.ObjectMapper;
import ifmg.edu.br.HOTELBAO.dtos.DailyInsertDTO;
import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.entities.Room;
import ifmg.edu.br.HOTELBAO.repository.ClientRepository;
import ifmg.edu.br.HOTELBAO.repository.RoomRepository;
import ifmg.edu.br.HOTELBAO.util.Factory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DailyResouceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Long existingId;
    private Long nonExistingId;

    private Room room;
    private Client client;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2000L;

        //save a valid room
        room = new Room();
        room.setDescription("room test");
        room.setImageUrl("imageUrl");
        room.setPrice(100);
        roomRepository.save(room);

        //save a valid client
        client = new Client();
        client.setName("client");
        client.setEmail("email@email.com");
        client.setPassword("password");
        client.setPhone("12345678910");
        clientRepository.save(client);
    }

    //Get all
    @Test
    @WithMockUser(username = "admin", roles = {})
    void findAllShouldReturnAllDailies() throws Exception {
        ResultActions result = mockMvc.perform(get("/daily").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        //verifica se retorna exatamente os 7 do migration
        result.andExpect(jsonPath("$.content.length()").value(8));
        result.andExpect(jsonPath("$.content[0].clientId").value(1));
        result.andExpect(jsonPath("$.content[0].room.description").value("Quarto simples com cama de solteiro e ventilador."));

    }

    //GetById
    @Test
    @WithMockUser(username = "admin", roles = {})
    void findByIdShouldReturnADailyIfIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/daily/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(1));
        result.andExpect(jsonPath("$.clientId").value(1));
        result.andExpect(jsonPath("$.room.description").value("Quarto simples com cama de solteiro e ventilador."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void findByIdShouldNotReturnADailyIfIdNotExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/daily/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Daily " + nonExistingId + " not found"));
    }

    //Insert
    @Test
    @WithMockUser(username = "admin", roles = {})
    void insertShouldCreateANewDaily() throws Exception {
        DailyInsertDTO daily = Factory.createDailyInsertDTO(client.getId(), room.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(daily.getDailyDate());
        String dtoJson = objectMapper.writeValueAsString(daily);

        //força o formato da data pra terminar com fuso correto
        if (formattedDate.endsWith("Z")) {
            formattedDate = formattedDate.substring(0, formattedDate.length() -1) + "+00:00";
        }

        ResultActions result = mockMvc.perform(post("/daily").accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.dailyDate").value(formattedDate));
        result.andExpect(jsonPath("$.room.id").value(room.getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void insertShouldNotCreateANewDailyIfDataIsInvalid() throws Exception {
        DailyInsertDTO daily = Factory.createDailyInsertDTO(client.getId(), room.getId());
        daily.setClient(nonExistingId);
        String dtoJson = objectMapper.writeValueAsString(daily);

        ResultActions result = mockMvc.perform(post("/daily").accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Client not found: " + nonExistingId));
    }

    //Update
    @Test
    @WithMockUser(username = "admin", roles = {})
    void updateShouldUpdateADaily() throws Exception {
        DailyInsertDTO daily = Factory.createDailyInsertDTO(client.getId(), room.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(daily.getDailyDate());
        String dtoJson = objectMapper.writeValueAsString(daily);

        //força o formato da data pra terminar com fuso correto
        if (formattedDate.endsWith("Z")) {
            formattedDate = formattedDate.substring(0, formattedDate.length() -1) + "+00:00";
        }

        ResultActions result = mockMvc.perform(put("/daily/{id}", existingId).accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.dailyDate").value(formattedDate));
        result.andExpect(jsonPath("$.room.id").value(room.getId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void updateShouldNotUpdateADailyIfDataIsInvalid() throws Exception {
        DailyInsertDTO daily = Factory.createDailyInsertDTO(client.getId(), room.getId());
        daily.setClient(nonExistingId);
        String dtoJson = objectMapper.writeValueAsString(daily);

        ResultActions result = mockMvc.perform(put("/daily/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Client not found: " + nonExistingId));
    }

    //Delete
    @Test
    @WithMockUser(username = "admin", roles = {})
    void deleteShouldDeleteADailyIfIdIsValid() throws Exception {
        ResultActions result = mockMvc.perform(delete("/daily/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void deleteShouldNotDeleteADailyIfIdIsInvalid() throws Exception {
        ResultActions result = mockMvc.perform(delete("/daily/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Daily not found: " + nonExistingId));
    }
}
