package ifmg.edu.br.HOTELBAO.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifmg.edu.br.HOTELBAO.dtos.EmailDTO;
import ifmg.edu.br.HOTELBAO.util.Factory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EmailResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void sendEmailShouldSendEmailIfEmailIsValid() throws Exception {
        EmailDTO email = Factory.createEmailDTO();
        String dtoJson = objectMapper.writeValueAsString(email);

        ResultActions result = mockMvc.perform(post("/email")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "client", roles = {"CLIENT"})
    void sendEmailShouldNotSendEmailIfEmailIsInvalid() throws Exception {
        EmailDTO email = Factory.createEmailDTO();
        email.setTo("aa");
        String dtoJson = objectMapper.writeValueAsString(email);

        ResultActions result = mockMvc.perform(post("/email")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.fieldMessages[0].message").value("must be a well-formed email address"));
    }
}
