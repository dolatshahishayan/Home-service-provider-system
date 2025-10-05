package ir.maktabsharif.home_service.controller.expert;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.hamcrest.Matchers;
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

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
class ExpertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ExpertService expertService;
    @Autowired
    private ExpertMapper expertMapper;
    @Autowired
    private JwtUtil mockJwtUtil;
    @Autowired
    private SecurityContextUtil securityContextUtil;


    private Expert expert;
    private ExpertFindResponse expertFindResponse;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);
        user.setEmail("expert@test.com");

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(principal);

        expert = new Expert();
        expert.setId(1);
        expert.setFirstName("John");
        expert.setLastName("Doe");
        expert.setEmail("john.doe@example.com");
        expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
        expert.setScore(BigDecimal.valueOf(4.7));
        expert.setIsEmailVerified(false);

        expertFindResponse = new ExpertFindResponse(
                expert.getId(),
                expert.getFirstName(),
                expert.getLastName(),
                expert.getExpertStatus(),
                expert.getScore().doubleValue(),
                expert.getIsEmailVerified()
        );
    }

    @Test
    void saveExpert_ShouldReturnExpertWithToken() throws Exception {
        ExpertSaveUpdateRequest request = new ExpertSaveUpdateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("123456");

        Mockito.when(expertService.register(any(ExpertSaveUpdateRequest.class))).thenReturn(expert);
        Mockito.when(expertMapper.mapToResponse(any(Expert.class))).thenReturn(expertFindResponse);
        Mockito.when(mockJwtUtil.generateToken(any(UserDetailsImpl.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/v1/experts/save")
                        .param("imagePath", "some/path.jpg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.expertStatus").value("WAITING_FOR_VERIFYING"))
                .andExpect(jsonPath("$.score").value(4.7))
                .andExpect(jsonPath("$.isEmailVerified").value(false));
    }

    @Test
    void verifyExpert_ShouldReturnMessage() throws Exception {
        Mockito.doNothing().when(expertService).updateStatusToVerified(eq(1));

        mockMvc.perform(put("/api/v1/experts/verify")
                        .param("expertId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("expert verified"));
    }

    @Test
    void updateExpert_ShouldReturnUpdatedExpertWithToken() throws Exception {
        ExpertSaveUpdateRequest request = new ExpertSaveUpdateRequest();
        request.setFirstName("UpdatedName");
        request.setLastName("UpdatedLast");
        request.setEmail("john.doe@example.com");
        request.setPassword("newPassword");

        Expert updatedExpert = new Expert();
        updatedExpert.setId(1);
        updatedExpert.setFirstName("UpdatedName");
        updatedExpert.setLastName("UpdatedLast");
        updatedExpert.setEmail("john.doe@example.com");
        updatedExpert.setExpertStatus(ExpertStatus.VERIFIED);
        updatedExpert.setScore(BigDecimal.valueOf(4.8));
        updatedExpert.setIsEmailVerified(true);

        ExpertFindResponse updatedResponse = new ExpertFindResponse(
                updatedExpert.getId(),
                updatedExpert.getFirstName(),
                updatedExpert.getLastName(),
                updatedExpert.getExpertStatus(),
                updatedExpert.getScore().doubleValue(),
                updatedExpert.getIsEmailVerified()
        );

        Mockito.when(expertService.updateWithDTO(any(ExpertSaveUpdateRequest.class))).thenReturn(updatedExpert);
        Mockito.when(expertMapper.mapToResponse(any(Expert.class))).thenReturn(updatedResponse);
        Mockito.when(mockJwtUtil.generateToken(any(UserDetailsImpl.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(put("/api/v1/experts/update")
                        .param("imagePath", "new/path.jpg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.firstName").value("UpdatedName"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLast"))
                .andExpect(jsonPath("$.expertStatus").value("VERIFIED"))
                .andExpect(jsonPath("$.score").value(4.8))
                .andExpect(jsonPath("$.isEmailVerified").value(true));
    }

    @Test
    void findExpertByEmail_ShouldReturnExpert() throws Exception {
        String email = "john.doe@example.com";

        Mockito.when(expertService.findByEmail(eq(email))).thenReturn(expert);
        Mockito.when(expertMapper.mapToResponse(any(Expert.class))).thenReturn(expertFindResponse);

        mockMvc.perform(get("/api/v1/experts/find-by-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.expertStatus").value("WAITING_FOR_VERIFYING"))
                .andExpect(jsonPath("$.score").value(4.7))
                .andExpect(jsonPath("$.isEmailVerified").value(false));
    }
}
