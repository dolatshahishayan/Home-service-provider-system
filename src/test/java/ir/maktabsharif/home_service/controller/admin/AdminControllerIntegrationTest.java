package ir.maktabsharif.home_service.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.admin.AdminService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        userService.deleteAll();
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setRole(Role.ROLE_ADMIN);
        user.setIsEmailVerified(true);
        userService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        adminToken = jwtUtil.generateToken(userDetails);
    }

    @AfterAll
    void deleteUsers() {
        userService.deleteAll();
    }

    @Test
    void saveAdmin_shouldReturnAdminAndAuthorizationHeader() throws Exception {
        AdminSaveUpdateRequest request = new AdminSaveUpdateRequest();
        request.setEmail("test");
        request.setPassword("password");
        request.setFirstName("admin1");

        mockMvc.perform(post("/api/v1/admins/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.firstName").value("admin1"));
    }

    @Test
    void updateAdmin_shouldReturnUpdatedAdminAndAuthorizationHeader() throws Exception {
        AdminSaveUpdateRequest saveRequest = new AdminSaveUpdateRequest();
        saveRequest.setEmail("adminToUpdate");
        saveRequest.setFirstName("adminToUpdateName");
        saveRequest.setPassword("oldPassword");
        Admin savedAdmin = adminService.saveWithDTO(saveRequest);

        AdminSaveUpdateRequest updateRequest = new AdminSaveUpdateRequest();
        updateRequest.setId(savedAdmin.getId());
        updateRequest.setEmail("adminUpdated");
        updateRequest.setPassword("newPassword");
        updateRequest.setFirstName("adminUpdatedName");

        mockMvc.perform(put("/api/v1/admins/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.firstName").value("adminUpdatedName"));
    }
}
