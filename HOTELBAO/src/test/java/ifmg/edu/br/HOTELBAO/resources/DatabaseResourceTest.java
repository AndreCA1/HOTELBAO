package ifmg.edu.br.HOTELBAO.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DatabaseResourceTest {
    @Autowired
    private MockMvc mockMvc;

    //Delete
    @Test
    @WithMockUser(username = "admin", roles = {})
    void deleteShouldDeleteDatabaseIfConfimationIsValid() throws Exception {
        ResultActions result = mockMvc.perform(get("/database/delete?confirmation={CONFIRMATION}", "CONFIRM_DELETE").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.message").value("Database was sucessfully excluded!"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {})
    void deleteShouldNotDeleteDatabaseIfConfimationIsInvalid() throws Exception {
        ResultActions result = mockMvc.perform(get("/database/delete?confirmation={CONFIRMATION}", "CONFIRM").accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.message").value("Invalid argument !"));
    }
}
