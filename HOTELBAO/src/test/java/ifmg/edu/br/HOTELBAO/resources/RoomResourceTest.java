package ifmg.edu.br.HOTELBAO.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmg.edu.br.HOTELBAO.dtos.RoomDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoomResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2000L;
    }

    //Get all
    @Test
    @WithMockUser(username = "admin", roles = {})
    void findAllShouldReturnAllRooms() throws Exception {
        ResultActions result = mockMvc.perform(get("/room").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        //verifica se retorna exatamente os 5 do migration
        result.andExpect(jsonPath("$.content.length()").value(5));
        result.andExpect(jsonPath("$.content[0].description").value("Quarto simples com cama de solteiro e ventilador."));

    }

    //GetById
    @Test
    @WithMockUser(username = "admin", roles = {})
    void findByIdShouldReturnARoomIfIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/room/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(1));
        result.andExpect(jsonPath("$.description").value("Quarto simples com cama de solteiro e ventilador."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void findByIdShouldNotReturnARoomIfIdNotExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/room/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Room " + nonExistingId + " not found"));
    }

    //Insert
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void insertShouldCreateANewRoom() throws Exception {
        RoomDTO room = Factory.createRoomDTO();
        String dtoJson = objectMapper.writeValueAsString(room);

        ResultActions result = mockMvc.perform(post("/room").accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.description").value("teste"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void insertShouldNotCreateANewRoomIfDataIsInvalid() throws Exception {
        RoomDTO room = Factory.createRoomDTO();
        room.setPrice(-1);
        String dtoJson = objectMapper.writeValueAsString(room);

        ResultActions result = mockMvc.perform(post("/room").accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.fieldMessages[0].message").value("Preço deve ter valor positivo"));
    }

    //Update
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateShouldUpdateIfIdIsValid() throws Exception {
        RoomDTO room = Factory.createRoomDTO();
        String dtoJson = objectMapper.writeValueAsString(room);

        ResultActions result = mockMvc.perform(put("/room/{id}", existingId).accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.description").value("teste"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateShouldNotUpdateIfDataIsInvalid() throws Exception {
        RoomDTO room = Factory.createRoomDTO();
        room.setPrice(0);
        String dtoJson = objectMapper.writeValueAsString(room);

        ResultActions result = mockMvc.perform(put("/room/{id}", existingId).accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.fieldMessages[0].message").value("Preço deve ter valor positivo"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateShouldNotUpdateIfIdIsNotValid() throws Exception {
        RoomDTO room = Factory.createRoomDTO();
        String dtoJson = objectMapper.writeValueAsString(room);

        ResultActions result = mockMvc.perform(put("/room/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Room not found: " + nonExistingId));
    }

    //Delete
    @Test
    @WithMockUser(username = "client", roles = {"ADMIN"})
    void deleteShouldDeleteAUserIfIdExists() throws Exception {
        ResultActions result = mockMvc.perform(delete("/room/{id}", existingId));

        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "client", roles = {"ADMIN"})
    void deleteShouldNotDeleteAUserIfIdNotExists() throws Exception {
        ResultActions result = mockMvc.perform(delete("/room/{id}", nonExistingId));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Room not found: " + nonExistingId));
    }
}