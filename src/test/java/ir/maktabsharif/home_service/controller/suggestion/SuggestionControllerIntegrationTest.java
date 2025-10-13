package ir.maktabsharif.home_service.controller.suggestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.expert_service.ExpertServiceId;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestMockConfig.class)
class SuggestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SuggestionService suggestionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ExpertService expertService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ExpertServiceService expertServiceService;
    private String customerToken;
    private String expertToken;
    private Expert expertTest;
    private Customer customerTest;
    private Service serviceEntity;
    private Order orderTest;
    private Suggestion suggestion;

    @BeforeEach
    void setup() {
        Expert user = new Expert();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_EXPERT);
        user.setExpertStatus(ExpertStatus.VERIFIED);
        expertTest = expertService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        expertToken = jwtUtil.generateToken(userDetails);

        Customer user3 = new Customer();
        user3.setEmail("user3@test.com");
        user3.setPassword(passwordEncoder.encode("test"));
        user3.setIsEmailVerified(true);
        user3.setRole(Role.ROLE_CUSTOMER);
        customerTest = customerService.save(user3);
        UserDetails userDetails3 = userService.loadUserByUsername(user3.getEmail());
        customerToken = jwtUtil.generateToken(userDetails3);
        serviceEntity = new Service();
        serviceEntity.setName("Test Service");
        serviceEntity.setBasePrice(BigDecimal.valueOf(100.0));
        serviceEntity.setDescription("Test Description");
        serviceEntity = serviceService.save(serviceEntity);
        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setCustomer(customerTest);
        order.setDescription("Painting");
        order.setService(serviceEntity);
        order.setFinalPrice(BigDecimal.valueOf(20000000));
        order.setProposedPrice(BigDecimal.valueOf(20000000));
        orderTest=orderService.save(order);

        ExpertServiceId expertServiceI=new ExpertServiceId(expertTest.getId(),serviceEntity.getId());
        ir.maktabsharif.home_service.model.expert_service.ExpertService expertService1=new ir.maktabsharif.home_service.model.expert_service.ExpertService();
        expertService1.setService(serviceEntity);
        expertService1.setId(expertServiceI);
        expertService1.setExpert(user);
        expertServiceService.save(expertService1);

        suggestion=new Suggestion();
        suggestion.setAccepted(true);
        suggestion.setPrice(BigDecimal.valueOf(2000000000));
        suggestion.setOrder(orderTest);
        suggestion=suggestionService.save(suggestion);
    }

    @AfterEach
    void deleteUsers() {
        suggestionService.deleteAll();
        orderService.deleteAll();
        expertServiceService.deleteAll();
        serviceService.deleteAll();
        customerService.deleteAll();
        expertService.deleteAll();
    }

    @Test
    void saveSuggestion_ShouldReturnSavedSuggestion() throws Exception {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest(
                null, orderTest.getId(), "Test Description", 100.0, 2.0, LocalDateTime.now(), false
        );

        mockMvc.perform(post("/api/v1/suggestions/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void updateSuggestion_ShouldReturnUpdatedSuggestion() throws Exception {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest(
                suggestion.getId(), orderTest.getId(), "Updated Description", 150.0, 3.0, LocalDateTime.now(), true
        );

        mockMvc.perform(put("/api/v1/suggestions/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(suggestion.getId()))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void confirmSuggestionAcceptance_ShouldReturnSuccessMessage() throws Exception {
        String id=String.valueOf(suggestion.getId());
        mockMvc.perform(put("/api/v1/suggestions/confirm-suggestion-acceptance")
                        .param("suggestionId", id)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Suggestion's acceptance has been confirmed"));
    }

    @Test
    void findAllByExpertId_ShouldReturnPageOfSuggestions() throws Exception {
        suggestion.setExpert(expertTest);
        suggestionService.save(suggestion);
        String id=String.valueOf(expertTest.getId());
        mockMvc.perform(get("/api/v1/suggestions/find-all-by-expert-id")
                        .param("expertId", id)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(suggestion.getId()))
                .andExpect(jsonPath("$.content[0].expertId").value(expertTest.getId()))
                .andExpect(jsonPath("$.content[0].price").value(2000000000));
    }

    @Test
    void findAllAndSortByPriceAscending_ShouldReturnPagedSuggestions() throws Exception {
        String id=String.valueOf(orderTest.getId());

        mockMvc.perform(get("/api/v1/suggestions/find-all-and-sort-by-price-ascending")
                        .param("orderId", id)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(suggestion.getId()));
    }

    @Test
    void findAllAndSortByExpertScoreDescending_ShouldReturnPagedSuggestions() throws Exception {
        String id=String.valueOf(orderTest.getId());
        expertTest.setScore(BigDecimal.valueOf(3));
        suggestion.setExpert(expertTest);
        suggestionService.save(suggestion);
        mockMvc.perform(get("/api/v1/suggestions/find-all-and-sort-by-expert-score-descending")
                        .param("orderId", id)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(suggestion.getId()));
    }
}
