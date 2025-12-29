package ir.maktabsharif.home_service.controller.expert_service;

import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.expert_service.ExpertServiceId;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpertServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExpertServiceService expertServiceService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    private String adminToken;
    private Integer expertId;
    private Integer serviceId;

    @BeforeEach
    void setup() {
        expertServiceService.deleteAll();
        serviceService.deleteAll();
        expertService.deleteAll();
        userService.deleteAll();        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_ADMIN);
        userService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        adminToken = jwtUtil.generateToken(userDetails2);
        Expert expert = new Expert();
        Expert save = expertService.save(expert);
        expertId = save.getId();

        Service service = new Service();
        Service save1 = serviceService.save(service);
        serviceId = save1.getId();
        ExpertServiceId id = new ExpertServiceId(expertId, serviceId);

        ExpertService expertServiceEntity = new ExpertService();
        expertServiceEntity.setId(id);
        expertServiceEntity.setExpert(expert);
        expertServiceEntity.setService(service);
        expertServiceService.save(expertServiceEntity);
    }

    @AfterAll
    void deleteUsers() {
        expertServiceService.deleteAll();
        serviceService.deleteAll();
        expertService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void addExpertToService_ShouldReturnConfirmationMessage() throws Exception {

        Expert expert = new Expert();
        Expert save = expertService.save(expert);

        Service service = new Service();
        Service save1 = serviceService.save(service);
        String expertIdsString=save.getId().toString();
        String serviceIdString=save1.getId().toString();
        mockMvc.perform(post("/api/v1/expert-services/add-expert-to-service")
                        .param("expertId", expertIdsString)
                        .param("serviceId", serviceIdString)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("expert added to service"));
    }

    @Test
    void removeExpertFromService_ShouldReturnConfirmationMessage() throws Exception {

        String expertIdsString=expertId.toString();
        String serviceIdString=serviceId.toString();
        mockMvc.perform(delete("/api/v1/expert-services/remove-expert-from-service")
                        .param("expertId", expertIdsString)
                        .param("serviceId", serviceIdString)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("expert removed from service"));
    }

    @Test
    void findByExpertIdAndServiceId_ShouldReturnExpertService() throws Exception {


        String expertIdsString=expertId.toString();
        String serviceIdString=serviceId.toString();
        mockMvc.perform(get("/api/v1/expert-services/find-by-expert-id-and-service-id")
                        .param("expertId", expertIdsString)
                        .param("serviceId", serviceIdString)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expertId").value(expertId))
                .andExpect(jsonPath("$.serviceId").value(serviceId));
    }

    @Test
    void existsByServiceIdAndExpertId_ShouldReturnBoolean() throws Exception {

        String expertIdsString=expertId.toString();
        String serviceIdString=serviceId.toString();
        mockMvc.perform(get("/api/v1/expert-services/exists-by-service-id-and-expert-id")
                        .param("serviceId", serviceIdString)
                        .param("expertId", expertIdsString)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByExpertId_ShouldReturnPagedExpertService() throws Exception {


        String expertIdsString=expertId.toString();
        mockMvc.perform(get("/api/v1/expert-services/find-by-expert-id")
                        .param("expertId", expertIdsString)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].expertId").value(expertId))
                .andExpect(jsonPath("$.content[0].serviceId").value(serviceId));
    }
}
