package ir.maktabsharif.home_service.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.comment.CommentFindResponse;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.comment.CommentService;
import ir.maktabsharif.home_service.service.user.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentMapper commentMapper;

    private String customerToken;
    private String expertToken;
    private User savedExpert;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_CUSTOMER);
        userService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        customerToken = jwtUtil.generateToken(userDetails);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_EXPERT);
        savedExpert=userService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        expertToken = jwtUtil.generateToken(userDetails2);
    }

    @AfterAll
    void deleteUsers() {
        userService.deleteAll();
    }

    @Test
    void saveComment_shouldReturnSavedComment() throws Exception {
        CommentSaveUpdateRequest request = new CommentSaveUpdateRequest();
        request.setContext("Nice job!");

        Comment savedComment = new Comment();
        savedComment.setId(10);
        savedComment.setContext("Nice job!");

        CommentFindResponse response = new CommentFindResponse();
        response.setId(10);
        response.setContext("Nice job!");

        Mockito.when(commentService.saveWithDTO(any(), any())).thenReturn(savedComment);
        Mockito.when(commentMapper.mapToResponse(savedComment)).thenReturn(response);

        mockMvc.perform(post("/api/v1/comments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + customerToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.context").value("Nice job!"))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void existsByOrder_shouldReturnTrue() throws Exception {
        Mockito.when(commentService.existsByOrder(5)).thenReturn(true);

        mockMvc.perform(get("/api/v1/comments/exists-by-order")
                        .header("Authorization", "Bearer " + customerToken)
                        .param("orderId", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByOrder_shouldReturnComment() throws Exception {
        Comment comment = new Comment();
        comment.setId(7);
        comment.setContext("Great service!");

        CommentFindResponse response = new CommentFindResponse();
        response.setId(20);
        response.setContext("Great service!");

        Mockito.when(commentService.findByOrder(7)).thenReturn(comment);
        Mockito.when(commentMapper.mapToResponse(comment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/comments/find-by-order")
                        .param("orderId", "7")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.context").value("Great service!"));
    }

    @Test
    void viewExpertScoreByOrder_shouldReturnScore() throws Exception {
        Mockito.when(commentService.viewExpertScoreByOrder(3)).thenReturn(4.5);

        mockMvc.perform(get("/api/v1/comments/view-expert-score-by-order")
                        .param("orderId", "3")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void viewAverageScore_shouldReturnAverage() throws Exception {
        Mockito.when(commentService.viewExpertAverageScore(savedExpert.getId())).thenReturn(3.7);
        mockMvc.perform(get("/api/v1/comments/view-average-score")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(content().string("3.7"));
    }

}
