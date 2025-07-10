package ifmg.edu.br.HOTELBAO.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InvoiceResourceTest {
    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2000L;
    }

    //findClientById
    @Test
    @WithMockUser(username = "client", roles = {"ADMIN"})
    void invoiceShouldReturnAPdfWithValidReceipt() throws Exception {
        ResultActions result = mockMvc.perform(get("/invoice/client/{id}", existingId));

    result.andExpect(status().isOk());
    result.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE));
    result.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"filename\"; filename=\"nota-fiscal.pdf\""));
    }

    @Test
    @WithMockUser(username = "client", roles = {"ADMIN"})
    void updateShouldNotUpdateAUserIfDataIsInvalid() throws Exception {
        ResultActions result = mockMvc.perform(get("/invoice/client/{id}", nonExistingId));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Client " + nonExistingId + " not found"));
    }
}
