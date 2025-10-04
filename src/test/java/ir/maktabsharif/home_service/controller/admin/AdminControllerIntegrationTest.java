package ir.maktabsharif.home_service.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.service.admin.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminService adminService;


    @Test
    void saveAdmin_shouldReturnAdminAndAuthorizationHeader() throws Exception {
        AdminSaveUpdateRequest request = new AdminSaveUpdateRequest();
        request.setEmail("test");
        request.setPassword("password");
        request.setFirstName("admin1");

        mockMvc.perform(post("/api/v1/admins/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.firstName").value("adminUpdatedName"));
    }
}
