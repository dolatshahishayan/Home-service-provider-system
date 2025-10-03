package ir.maktabsharif.home_service.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.dto.comment.CommentFindResponse;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.comment.CommentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(CommentControllerIntegrationTest.MockConfig.class)
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SecurityContextUtil securityContextUtil;

    static class MockConfig {
        @Bean
        CommentService commentService() {
            return Mockito.mock(CommentService.class);
        }

        @Bean
        CommentMapper commentMapper() {
            return Mockito.mock(CommentMapper.class);
        }

        @Bean
        SecurityContextUtil securityContextUtil() {
            return Mockito.mock(SecurityContextUtil.class);
        }
    }

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@test.com");
        UserDetailsImpl principal = new UserDetailsImpl(user);

        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(principal);
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

        Mockito.when(commentService.saveWithDTO(any(), eq(1))).thenReturn(savedComment);
        Mockito.when(commentMapper.mapToResponse(savedComment)).thenReturn(response);

        mockMvc.perform(post("/api/v1/comments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.context").value("Nice job!"))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void existsByOrder_shouldReturnTrue() throws Exception {
        Mockito.when(commentService.existsByOrder(5)).thenReturn(true);

        mockMvc.perform(get("/api/v1/comments/exists-by-order")
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
        response.setId(20); // فرض کن mapper تغییر می‌ده
        response.setContext("Great service!");

        Mockito.when(commentService.findByOrder(7)).thenReturn(comment);
        Mockito.when(commentMapper.mapToResponse(comment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/comments/find-by-order")
                        .param("orderId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.context").value("Great service!"));
    }

    @Test
    void viewExpertScoreByOrder_shouldReturnScore() throws Exception {
        Mockito.when(commentService.viewExpertScoreByOrder(3)).thenReturn(4.5);

        mockMvc.perform(get("/api/v1/comments/view-expert-score-by-order")
                        .param("orderId", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void viewAverageScore_shouldReturnAverage() throws Exception {
        Mockito.when(commentService.viewExpertAverageScore(1)).thenReturn(3.7);

        mockMvc.perform(get("/api/v1/comments/view-average-score"))
                .andExpect(status().isOk())
                .andExpect(content().string("3.7"));
    }

}
