package ir.maktabsharif.home_service.controller.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.dto.user.VerificationRequest;
import ir.maktabsharif.home_service.util.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(EmailControllerIntegrationTest.MockConfig.class)
public class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private ObjectMapper objectMapper;

    static class MockConfig {
        @Bean
        EmailUtil emailUtil() {
            return Mockito.mock(EmailUtil.class);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.doNothing().when(emailUtil).verifyToken(anyString());
    }

    @Test
    void verifyEmail_ShouldReturnSuccessMessage() throws Exception {
        VerificationRequest request = new VerificationRequest("test-token");

        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified"));
    }

}
