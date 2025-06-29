package ifmg.edu.br.HOTELBAO.auth;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;
    @Value(value = "${security.client-id}")
    String clientId;
    @Value(value = "${security.client-secret}")
    String clientSecret;

    @Test
    void adminLoginShouldReturnValidToken() throws Exception {
        ResultActions result = mockMvc.perform(post("/oauth2/token")
                .with(httpBasic(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("username", "ana.souza@email.com")
                .param("password", "123456"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.access_token").exists());
        result.andExpect(jsonPath("$.token_type").value("Bearer"));

        //get token
        String accessToken = JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.access_token");

        //Testa um endpoint protegido (get all client)
        mockMvc.perform(get("/client")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

    }

    @Test
    void clientLoginShouldReturnValidToken() throws Exception {
        ResultActions result = mockMvc.perform(post("/oauth2/token")
                .with(httpBasic(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("username", "carlos.lima@email.com")
                .param("password", "123456"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.access_token").exists());
        result.andExpect(jsonPath("$.token_type").value("Bearer"));

        //get token
        String accessToken = JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.access_token");

        //Testa um endpoint protegido (get all client)
        mockMvc.perform(get("/client")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void inexistentUserShouldReturnInvalidToken() throws Exception {
        ResultActions result = mockMvc.perform(post("/oauth2/token")
                .with(httpBasic(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("grant_type", "password")
                .param("username", "email@email.com")
                .param("password", "123456"));
        result.andExpect(status().isBadRequest());
    }
}
