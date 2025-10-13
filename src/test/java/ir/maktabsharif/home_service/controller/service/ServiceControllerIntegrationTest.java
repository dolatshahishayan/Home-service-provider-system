package ir.maktabsharif.home_service.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.User;
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
@Import(TestMockConfig.class)
class ServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    private String adminToken;
    private Service serviceEntity;
    private Service serviceEntity2;

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
        serviceEntity = new Service();
        serviceEntity.setName("Test Service");
        serviceEntity.setBasePrice(BigDecimal.valueOf(100.0));
        serviceEntity.setDescription("Test Description");
        serviceEntity = serviceService.save(serviceEntity);
        serviceEntity2 = new Service();
        serviceEntity2.setName("Test Service2");
        serviceEntity2.setBasePrice(BigDecimal.valueOf(100.0));
        serviceEntity2.setDescription("Test Description");
        serviceEntity2.setParentService(serviceEntity);
        serviceEntity2 = serviceService.save(serviceEntity2);
    }

    @AfterEach
    void deleteUsers() {
        serviceService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void saveService_ShouldReturnSavedService() throws Exception {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest();
        request.setName("Test Service1");
        request.setDescription("Test Description");
        request.setBasePrice(100.0);


        mockMvc.perform(post("/api/v1/services/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test service1"))
                .andExpect(jsonPath("$.basePrice").value(100.0))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void updateService_ShouldReturnUpdatedService() throws Exception {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest(serviceEntity.getId(), "Updated Service", 150.0, "Updated Description", null);


        mockMvc.perform(put("/api/v1/services/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated service"))
                .andExpect(jsonPath("$.basePrice").value(150.0))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void updateDescription_ShouldReturnOk() throws Exception {
        String id = String.valueOf(serviceEntity.getId());
        mockMvc.perform(put("/api/v1/services/update-description")
                        .param("serviceId", id)
                        .param("description", "New Description")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Description updated"));
    }

    @Test
    void updateBasePrice_ShouldReturnOk() throws Exception {
        String id = String.valueOf(serviceEntity.getId());

        mockMvc.perform(put("/api/v1/services/update-base-price")
                        .param("serviceId", id)
                        .param("basePrice", "200.0")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Base price updated"));
    }

    @Test
    void existsByName_ShouldReturnTrue() throws Exception {

        mockMvc.perform(get("/api/v1/services/exists-by-name")
                        .param("name", "Test Service")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findAllParentServices_ShouldReturnPagedServices() throws Exception {

        mockMvc.perform(get("/api/v1/services/find-all-parentServices")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllSubServices_ReturnsPagedSubServices() throws Exception {

        mockMvc.perform(get("/api/v1/services/find-all-subServices")
                        .param("serviceId", String.valueOf(serviceEntity.getId()))
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(serviceEntity2.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Test Service2"));
    }

    @Test
    void deleteService_ShouldReturnOk() throws Exception {
        String id = String.valueOf(serviceEntity.getId());
        mockMvc.perform(delete("/api/v1/services/delete")
                        .param("serviceId", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted service"));
    }
}
