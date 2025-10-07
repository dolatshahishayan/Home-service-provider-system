package ir.maktabsharif.home_service.controller.expert;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.EmailUtil;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ExpertService expertService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private EmailUtil emailUtil;

    private String expertToken;
    private String adminToken;
    private Expert save;

    @BeforeEach
    void setup() {

        userService.deleteAll();
        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_EXPERT);
        userService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        expertToken = jwtUtil.generateToken(userDetails);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_ADMIN);
        userService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        adminToken = jwtUtil.generateToken(userDetails2);

        Expert expert = new Expert();
        expert.setFirstName("John");
        expert.setLastName("Doe");
        expert.setEmail("john.doe2@example.com");
        expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
        expert.setScore(BigDecimal.valueOf(4.7));
        expert.setIsEmailVerified(false);
        save = expertService.save(expert);

    }

    @AfterEach
    void deleteUsers() {
        emailUtil.deleteAll();
        walletService.deleteAll();
        expertService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void saveExpert_ShouldReturnExpertWithToken() throws Exception {
        ExpertSaveUpdateRequest request = new ExpertSaveUpdateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("123456");

        mockMvc.perform(post("/api/v1/experts/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.expertStatus").value("NEW"))
                .andExpect(jsonPath("$.isEmailVerified").value(false));
    }

    @Test
    void verifyExpert_ShouldReturnMessage() throws Exception {
        ExpertSaveUpdateRequest request = new ExpertSaveUpdateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("123456");
        Expert register = expertService.register(request);
        String idString = String.valueOf(register.getId());
        mockMvc.perform(put("/api/v1/experts/verify")
                        .param("expertId", idString)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("expert verified"));
    }

    @Test
    void updateExpert_ShouldReturnUpdatedExpertWithToken() throws Exception {
        ExpertSaveUpdateRequest request = new ExpertSaveUpdateRequest();
        request.setFirstName("UpdatedName");
        request.setLastName("UpdatedLast");
        request.setEmail("john.doe4@example.com");
        request.setPassword("newPassword");
        request.setId(save.getId());

        mockMvc.perform(put("/api/v1/experts/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLast"))
                .andExpect(jsonPath("$.expertStatus").value("WAITING_FOR_VERIFYING"));
    }

    @Test
    void findExpertByEmail_ShouldReturnExpert() throws Exception {
        String email = "john.doe2@example.com";

        mockMvc.perform(get("/api/v1/experts/find-by-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(save.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.expertStatus").value("WAITING_FOR_VERIFYING"))
                .andExpect(jsonPath("$.score").value(4.7))
                .andExpect(jsonPath("$.isEmailVerified").value(false));
    }
}
