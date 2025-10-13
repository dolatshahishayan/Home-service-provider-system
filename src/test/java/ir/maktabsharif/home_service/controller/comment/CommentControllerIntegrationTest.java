package ir.maktabsharif.home_service.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.service.comment.CommentService;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestMockConfig.class)
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
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private ExpertService expertService;

    private String customerToken;
    private String expertToken;
    private Customer save;
    private Order savedOrder;
    private Suggestion save1;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        Customer user = new Customer();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_CUSTOMER);
        save = customerService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        customerToken = jwtUtil.generateToken(userDetails);

        Expert user2 = new Expert();
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_EXPERT);
        user2.setExpertStatus(ExpertStatus.VERIFIED);
        Expert savedExpert = expertService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        expertToken = jwtUtil.generateToken(userDetails2);

        Order order = new Order();
        order.setDescription("test");
        order.setOrderStatus(OrderStatus.PAYED);
        order.setCustomer(save);
        order.setExpert(savedExpert);
        savedOrder = orderService.save(order);

        Suggestion suggestion = new Suggestion();
        suggestion.setStartDate(LocalDateTime.now().minusMinutes(10));
        suggestion.setOrder(savedOrder);
        save1 = suggestionService.save(suggestion);

    }

    @AfterEach
    void deleteUsers() {
        commentService.deleteAll();
        suggestionService.delete(save1);
        orderService.delete(savedOrder);
        customerService.delete(save);
        userService.deleteAll();
    }

    @Test
    void saveComment_shouldReturnSavedComment() throws Exception {
        CommentSaveUpdateRequest request = new CommentSaveUpdateRequest();
        request.setContext("Nice job!");
        request.setExpertScore(3D);
        request.setOrderId(savedOrder.getId());

        mockMvc.perform(post("/api/v1/comments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + customerToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.context").value("Nice job!"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void existsByOrder_shouldReturnTrue() throws Exception {
        CommentSaveUpdateRequest request = new CommentSaveUpdateRequest();
        request.setContext("Nice job!");
        request.setExpertScore(3D);
        request.setOrderId(savedOrder.getId());
        commentService.saveWithDTO(request, save.getId());
        Integer id = savedOrder.getId();
        String idString = String.valueOf(id);

        mockMvc.perform(get("/api/v1/comments/exists-by-order")
                        .header("Authorization", "Bearer " + customerToken)
                        .param("orderId", idString))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByOrder_shouldReturnComment() throws Exception {
        CommentSaveUpdateRequest request = new CommentSaveUpdateRequest();
        request.setContext("Nice job!");
        request.setExpertScore(3D);
        request.setOrderId(savedOrder.getId());
        Comment comment = commentService.saveWithDTO(request, save.getId());
        Integer id = savedOrder.getId();
        String idString = String.valueOf(id);


        mockMvc.perform(get("/api/v1/comments/find-by-order")
                        .param("orderId", idString)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.context").value("Nice job!"));
    }

    @Test
    void viewExpertScoreByOrder_shouldReturnScore() throws Exception {
        CommentSaveUpdateRequest request = new CommentSaveUpdateRequest();
        request.setContext("Nice job!");
        request.setExpertScore(4.5D);
        request.setOrderId(savedOrder.getId());
        commentService.saveWithDTO(request, save.getId());
        Integer id = savedOrder.getId();
        String idString = String.valueOf(id);

        mockMvc.perform(get("/api/v1/comments/view-expert-score-by-order")
                        .param("orderId", idString)
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void viewAverageScore_shouldReturnAverage() throws Exception {
        CommentSaveUpdateRequest request = new CommentSaveUpdateRequest();
        request.setContext("Nice job!");
        request.setExpertScore(4.5D);
        request.setOrderId(savedOrder.getId());
        commentService.saveWithDTO(request, save.getId());
        mockMvc.perform(get("/api/v1/comments/view-average-score")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

}
