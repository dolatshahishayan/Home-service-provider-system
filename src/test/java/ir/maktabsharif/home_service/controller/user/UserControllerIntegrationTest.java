package ir.maktabsharif.home_service.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.user.UserServiceImpl;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ExpertServiceService expertServiceService;

    @Autowired
    private ExpertService expertService;

    @Autowired
    private ServiceService serviceService;

    private Service save;
    private Expert save1;
    private String adminToken;
    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user2 = new User();
        user2.setEmail("test@example.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_ADMIN);
        userService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        adminToken = jwtUtil.generateToken(userDetails2);

        Expert expert = new Expert();
        expert.setEmail("ali@test.com");
        expert.setFirstName("Ali");
        expert.setLastName("Dolatshahi");
        expert.setExpertStatus(ExpertStatus.VERIFIED);
        expert.setPassword(passwordEncoder.encode("test"));
        expert.setIsEmailVerified(true);
        expert.setRole(Role.ROLE_EXPERT);
        save1 = expertService.save(expert);
        Service service = new Service();
        service.setName("Painting");
        service.setDescription("painting");
        service.setBasePrice(BigDecimal.valueOf(2000000));
        save = serviceService.save(service);

        expertServiceService.addExpertToService(save1.getId(), save.getId());

    }

    @AfterEach
    void deleteData(){
        expertServiceService.delete(expertServiceService.findByExpertId(save1.getId(), PageRequest.of(0, 10)).getContent().get(0));
        expertService.deleteById(save1.getId());
        userService.deleteAll();
        serviceService.deleteById(save.getId());
    }


    @Test
    void existsByEmail_ReturnsTrue() throws Exception {
        mockMvc.perform(get("/api/v1/users/exists-by-email")
                        .param("email", "test@example.com")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByEmailAndIdNot_ReturnsFalse() throws Exception {
        mockMvc.perform(get("/api/v1/users/exists-by-email-and-id-not")
                        .param("email", "test@example.com")
                        .param("id", "100")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void searchUsers_ReturnsPagedResults() throws Exception {
        UserSearchRequestDTO requestDTO = new UserSearchRequestDTO();
        requestDTO.setName("Ali");
        requestDTO.setRole(Role.ROLE_EXPERT);
        requestDTO.setServiceIds(List.of(save.getId()));

        mockMvc.perform(post("/api/v1/users/search-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(save1.getId()))
                .andExpect(jsonPath("$.content[0].firstName").value("Ali"))
                .andExpect(jsonPath("$.content[0].role").value("ROLE_EXPERT"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchUsers_WithEmptyRequest_ReturnsEmptyPage() throws Exception {
        UserSearchRequestDTO requestDTO = new UserSearchRequestDTO();

        mockMvc.perform(post("/api/v1/users/search-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

}
