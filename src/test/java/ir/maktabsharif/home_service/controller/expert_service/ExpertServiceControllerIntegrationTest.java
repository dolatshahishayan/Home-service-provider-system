package ir.maktabsharif.home_service.controller.expert_service;

import ir.maktabsharif.home_service.TestMockConfig;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import(TestMockConfig.class)
class ExpertServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExpertServiceService expertServiceService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private UserService userService;

    private String adminToken;
    private Expert expert1;
    private Expert expertTest2;
    private Service service1;
    private Service serviceTest2;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_ADMIN);
        userService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        adminToken = jwtUtil.generateToken(userDetails2);
        Expert expert = new Expert();
        expert.setFirstName("test1");
        Expert expert2 = new Expert();
        expert2.setFirstName("test2");
        expert1 = expertService.save(expert2);
        expertTest2 = expertService.save(expert);
        Service service = new Service();
        service.setName("test3");
        Service service2 = new Service();
        service2.setName("test4");
        service1 = serviceService.save(service2);
        serviceTest2 = serviceService.save(service);
        ExpertServiceId id = new ExpertServiceId(expert1.getId(), service1.getId());
        ExpertService expertServiceEntity = new ExpertService();
        expertServiceEntity.setId(id);
        expertServiceEntity.setExpert(expert1);
        expertServiceEntity.setService(service1);
        expertServiceService.save(expertServiceEntity);
    }

    @AfterEach
    void deleteUsers() {
        expertServiceService.deleteAll();
        expertService.deleteAll();
        serviceService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void addExpertToService_ShouldReturnConfirmationMessage() throws Exception {
        String expertId = String.valueOf(expertTest2.getId());
        String serviceId = String.valueOf(serviceTest2.getId());
        mockMvc.perform(post("/api/v1/expert-services/add-expert-to-service")
                        .param("expertId", expertId)
                        .param("serviceId", serviceId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("expert added to service"));
    }

    @Test
    void removeExpertFromService_ShouldReturnConfirmationMessage() throws Exception {
        String expertId = String.valueOf(expert1.getId());
        String serviceId = String.valueOf(service1.getId());

        mockMvc.perform(delete("/api/v1/expert-services/remove-expert-from-service")
                        .param("expertId", expertId)
                        .param("serviceId", serviceId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("expert removed from service"));
    }

    @Test
    void findByExpertIdAndServiceId_ShouldReturnExpertService() throws Exception {
        String expertId = String.valueOf(expert1.getId());
        String serviceId = String.valueOf(service1.getId());

        mockMvc.perform(get("/api/v1/expert-services/find-by-expert-id-and-service-id")
                        .param("expertId", expertId)
                        .param("serviceId", serviceId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expertId").value(expert1.getId()))
                .andExpect(jsonPath("$.serviceId").value(service1.getId()));
    }

    @Test
    void existsByServiceIdAndExpertId_ShouldReturnBoolean() throws Exception {
        String expertId = String.valueOf(expert1.getId());
        String serviceId = String.valueOf(service1.getId());
        mockMvc.perform(get("/api/v1/expert-services/exists-by-service-id-and-expert-id")
                        .param("serviceId", serviceId)
                        .param("expertId", expertId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByExpertId_ShouldReturnPagedExpertService() throws Exception {
        String expertId = String.valueOf(expert1.getId());

        mockMvc.perform(get("/api/v1/expert-services/find-by-expert-id")
                        .param("expertId", expertId)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].expertId").value(expert1.getId()))
                .andExpect(jsonPath("$.content[0].serviceId").value(service1.getId()));
    }
}
