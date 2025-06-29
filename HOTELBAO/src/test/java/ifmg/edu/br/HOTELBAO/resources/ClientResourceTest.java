package ifmg.edu.br.HOTELBAO.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmg.edu.br.HOTELBAO.dtos.ClientInsertDTO;
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
public class ClientResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private String existingEmail;
    private String nonExistingEmail;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2000L;
        existingEmail = "ana.souza@email.com";
        nonExistingEmail = "email.email.com";
    }

    //Find all
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findAllShouldReturnAllClientsIfUserIsAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get("/client").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        //verifica se retorna exatamente os 5 do migration
        result.andExpect(jsonPath("$.content.length()").value(5));
        result.andExpect(jsonPath("$.content[0].name").value("Ana Souza"));
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void findAllShouldNotReturnAllClientsIfUserIsClient() throws Exception {
        ResultActions result = mockMvc.perform(get("/client").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    // Find by id
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findByIdShouldReturnAClientIfUserIsAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get("/client/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(1));
        result.andExpect(jsonPath("$.name").value("Ana Souza"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findByIdShouldReturnAnErrorIfIdNotExistsAndUserIsAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get("/client/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.error").value("Resource not found"));
        result.andExpect(jsonPath("$.message").value("Client " + nonExistingId + " not found"));
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void findByIdShouldNotReturnAClientIfUserIsClient() throws Exception {
        ResultActions result = mockMvc.perform(get("/client/1").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }


    //FindByEmail
    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void findByEmailShouldReturnAClientIfUserIsAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get("/client/email/{email}", existingEmail).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(1));
        result.andExpect(jsonPath("$.name").value("Ana Souza"));
        result.andExpect(jsonPath("$.roles.length()").value(2));
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void findByEmailShouldNotReturnAValidClientIfEmailNotExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/client/email/{email}", nonExistingEmail).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.error").value("Resource not found"));
        result.andExpect(jsonPath("$.message").value("Client with email: " + nonExistingEmail + " not found"));
    }

    @Test
    @WithMockUser(username = "client", roles = {})
    void findByEmailIsForbiddenIfUserNotAuthenticated() throws Exception {
        ResultActions result = mockMvc.perform(get("/client/email/{email}", nonExistingEmail).accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    //Insert
    @Test
    @WithMockUser(username = "client", roles = {})
    void insertShouldCreateANewUserIfDataIsValid() throws Exception {
        ClientInsertDTO client = Factory.createClientInsertDTO();
        String dtoJson = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(post("/client")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.name").value("client"));
        result.andExpect(jsonPath("$.roles.length()").value(1));
    }

    @Test
    @WithMockUser(username = "client", roles = {})
    void insertShouldNotCreateANewUserIfEmailIsBlank() throws Exception {
        ClientInsertDTO client = Factory.createClientInsertDTO();
        client.setEmail("");
        String dtoJson = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(post("/client")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.fieldMessages[0].message").value("Email não pode ser vazio"));
    }

    @Test
    @WithMockUser(username = "client", roles = {})
    void insertShouldNotCreateANewUserIfEmailIsInvalid() throws Exception {
        ClientInsertDTO client = Factory.createClientInsertDTO();
        client.setEmail("abc");
        String dtoJson = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(post("/client")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.fieldMessages[0].message").value("Favor informar Email válido"));
    }

    //Signup

    //Update
    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void updateShouldUpdateAUserIfIdExists() throws Exception {
        ClientInsertDTO client = Factory.createClientInsertDTO();
        String dtoJson = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(put("/client/{id}", existingId)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
        result.andExpect(jsonPath("$.name").value("client"));
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void updateShouldNotUpdateAUserIfDataIsInvalid() throws Exception {
        ClientInsertDTO client = Factory.createClientInsertDTO();
        client.setEmail("");
        String dtoJson = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(put("/client/{id}", existingId)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.fieldMessages[0].message").value("Email não pode ser vazio"));
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void updateShouldNotUpdateAUserIfIdNotExists() throws Exception {
        ClientInsertDTO client = Factory.createClientInsertDTO();
        String dtoJson = objectMapper.writeValueAsString(client);

        ResultActions result = mockMvc.perform(put("/client/{id}", nonExistingId)
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Client not found: " + nonExistingId));
    }

    //Delete
    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void deleteShouldDeleteAUserIfIdExists() throws Exception {
        ResultActions result = mockMvc.perform(delete("/client/{id}", existingId));

        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void deleteShouldNotDeleteAUserIfIdNotExists() throws Exception {
        ResultActions result = mockMvc.perform(delete("/client/{id}", nonExistingId));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Client not found: " + nonExistingId));
    }
}
