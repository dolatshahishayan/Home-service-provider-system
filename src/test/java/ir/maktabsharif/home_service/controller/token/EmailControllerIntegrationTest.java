package ir.maktabsharif.home_service.controller.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.user.VerificationRequest;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.util.EmailUtil;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestMockConfig.class)
public class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    private EmailVerificationToken token;
    private String customerToken;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setPassword(passwordEncoder.encode("test"));
        user3.setIsEmailVerified(true);
        user3.setRole(Role.ROLE_CUSTOMER);
        userService.save(user3);
        UserDetails userDetails3 = userService.loadUserByUsername(user3.getEmail());
        customerToken = jwtUtil.generateToken(userDetails3);
        token = emailUtil.createToken(user3, 10);
    }

    @AfterAll
    void deleteUsers() {
        emailUtil.deleteAll();
        userService.deleteAll();
    }

    @Test
    void verifyEmail_ShouldReturnSuccessMessage() throws Exception {
        VerificationRequest request = new VerificationRequest(token.getToken());

        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified"));
    }

}
