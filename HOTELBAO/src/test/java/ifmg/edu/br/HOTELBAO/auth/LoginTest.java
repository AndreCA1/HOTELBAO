package ifmg.edu.br.HOTELBAO.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import ifmg.edu.br.HOTELBAO.dtos.NewPasswordDTO;
import ifmg.edu.br.HOTELBAO.dtos.RequestTokenDTO;
import ifmg.edu.br.HOTELBAO.entities.PasswordRecover;
import ifmg.edu.br.HOTELBAO.services.AuthService;
import ifmg.edu.br.HOTELBAO.util.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Value(value = "${security.client-id}")
    String clientId;
    @Value(value = "${security.client-secret}")
    String clientSecret;


    private String existingEmail;
    private String nonExistingEmail;

    @BeforeEach
    void setUp() {
        existingEmail = "ana.souza@email.com";
        nonExistingEmail = "email@email.com";
    }

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

    @Test
    void createRecoverTokenShouldReturnAValidTokenIfEmailIsValid() throws Exception {
        RequestTokenDTO email = Factory.createRequestTokenDTO(existingEmail);
        String dtoJson = objectMapper.writeValueAsString(email);
        ResultActions result = mockMvc.perform(post("/auth/recover-token")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    void createRecoverTokenShouldRaiseErrorIfAInvalidEmailIsProvided() throws Exception {
        RequestTokenDTO email = Factory.createRequestTokenDTO(nonExistingEmail);
        String dtoJson = objectMapper.writeValueAsString(email);
        ResultActions result = mockMvc.perform(post("/auth/recover-token")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.error").value("Resource not found"));
        result.andExpect(jsonPath("$.message").value("Email not found!"));
    }

    @Test
    void recoverPasswordShouldChangeAPasswordIfTokenIsValid() throws Exception {
        PasswordRecover passwordRecover = authService.saveRecoverToken(existingEmail);
        NewPasswordDTO password = Factory.createNewPasswordDTO(passwordRecover.getToken());
        String dtoJson = objectMapper.writeValueAsString(password);
        ResultActions result = mockMvc.perform(post("/auth/reset-password")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    void recoverPasswordShouldNotChangeAPasswordIfTokenIsInvalid() throws Exception {
        String token = "";
        NewPasswordDTO password = Factory.createNewPasswordDTO(token);
        String dtoJson = objectMapper.writeValueAsString(password);
        ResultActions result = mockMvc.perform(post("/auth/reset-password")
                .content(dtoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.error").value("Resource not found"));
        result.andExpect(jsonPath("$.message").value("Token not found"));
    }
}
