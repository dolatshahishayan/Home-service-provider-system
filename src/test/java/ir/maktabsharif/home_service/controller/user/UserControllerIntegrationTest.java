package ir.maktabsharif.home_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private UserService userService;


    @Test
    void existsByEmail_ReturnsTrue() throws Exception {
        Mockito.when(userService.existsByEmail("test@example.com")).thenReturn(true);

        mockMvc.perform(get("/api/v1/users/exists-by-email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByEmailAndIdNot_ReturnsFalse() throws Exception {
        Mockito.when(userService.existsByEmailAndIdNot("test@example.com", 1)).thenReturn(false);

        mockMvc.perform(get("/api/v1/users/exists-by-email-and-id-not")
                        .param("email", "test@example.com")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void searchUsers_ReturnsPagedResults() throws Exception {
        UserSearchRequestDTO requestDTO = new UserSearchRequestDTO(
                Role.ROLE_EXPERT,
                "Ali",
                List.of(1, 2),
                3.0,
                5.0
        );

        UserSearchResponseDTO responseDTO = new UserSearchResponseDTO(
                1, "Ali", "Dolatshahi", "ali@test.com", Role.ROLE_EXPERT, 4.5, "ACTIVE"
        );

        Page<UserSearchResponseDTO> pageResult = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1);

        Mockito.when(userService.searchUsers(
                        Mockito.any(UserSearchRequestDTO.class),
                        Mockito.any(PageRequest.class)))
                .thenReturn(pageResult);


        mockMvc.perform(post("/api/v1/users/search-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Ali"))
                .andExpect(jsonPath("$.content[0].role").value("ROLE_EXPERT"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchUsers_WithEmptyRequest_ReturnsEmptyPage() throws Exception {
        UserSearchRequestDTO requestDTO = new UserSearchRequestDTO();

        Page<UserSearchResponseDTO> emptyPage = new PageImpl<>(List.of());

        Mockito.when(userService.searchUsers(Mockito.any(), Mockito.any(PageRequest.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(post("/api/v1/users/search-users") // باید POST باشه
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

}
