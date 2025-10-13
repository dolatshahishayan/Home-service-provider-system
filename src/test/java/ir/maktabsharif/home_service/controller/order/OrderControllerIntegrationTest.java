package ir.maktabsharif.home_service.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.service.ServiceService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestMockConfig.class)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ExpertService expertService;
    @Autowired
    private SuggestionService suggestionService;

    private String adminToken;
    private String customerToken;
    private String expertToken;
    private Order orderEntity;
    private Service serviceTest;
    private Order save;
    private Expert expertTest;
    private Suggestion save1;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        Expert user = new Expert();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_EXPERT);
        user.setExpertStatus(ExpertStatus.VERIFIED);
        expertTest = expertService.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        expertToken = jwtUtil.generateToken(userDetails);

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("test"));
        user2.setIsEmailVerified(true);
        user2.setRole(Role.ROLE_ADMIN);
        userService.save(user2);
        UserDetails userDetails2 = userService.loadUserByUsername(user2.getEmail());
        adminToken = jwtUtil.generateToken(userDetails2);

        Customer user3 = new Customer();
        user3.setEmail("user3@test.com");
        user3.setPassword(passwordEncoder.encode("test"));
        user3.setIsEmailVerified(true);
        user3.setRole(Role.ROLE_CUSTOMER);
        customerService.save(user3);
        UserDetails userDetails3 = userService.loadUserByUsername(user3.getEmail());
        customerToken = jwtUtil.generateToken(userDetails3);

        Service service = new Service();
        service.setBasePrice(BigDecimal.valueOf(200000));
        serviceTest = serviceService.save(service);

        orderEntity = new Order();
        orderEntity.setDescription("Test Order");
        orderEntity.setProposedPrice(BigDecimal.valueOf(100.0));
        orderEntity.setStartDate(LocalDateTime.now());
        orderEntity.setAddress("Test Address");
        orderEntity.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        orderEntity.setCustomer(user3);
        orderEntity.setService(service);
        orderEntity.setCreationDate(LocalDateTime.now());
        orderEntity.setFinalPrice(BigDecimal.valueOf(120.0));
        save = orderService.save(orderEntity);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(save);
        suggestion.setExpert(expertTest);
        suggestion.setPrice(BigDecimal.valueOf(20000000));
        save1 = suggestionService.save(suggestion);
    }

    @AfterEach
    void deleteUsers() {
        suggestionService.deleteAll();
        orderService.deleteAll();
        serviceService.deleteAll();
        customerService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void saveOrder_ShouldReturnOrderFindResponse() throws Exception {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setDescription("Test Order");
        request.setServiceId(serviceTest.getId());
        request.setProposedPrice(200000D);

        mockMvc.perform(post("/api/v1/orders/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Test Order"));
    }

    @Test
    void updateOrder_ShouldReturnOrderFindResponse() throws Exception {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setDescription("Updated Order");
        request.setId(save.getId());

        mockMvc.perform(put("/api/v1/orders/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(save.getId()))
                .andExpect(jsonPath("$.description").value("Updated Order"));
    }

    @Test
    void existsByExpertAndOrderStatusIn_ShouldReturnBoolean() throws Exception {
        String id = String.valueOf(expertTest.getId());
        orderEntity.setExpert(expertTest);
        orderService.save(orderEntity);
        mockMvc.perform(get("/api/v1/orders/exists-by-expert-and-order-status-in")
                        .param("expertId", id)
                        .param("orderStatuses", "WAITING_FOR_EXPERT_SUGGESTION")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByServiceId_ShouldReturnPagedOrders() throws Exception {
        String id = String.valueOf(serviceTest.getId());

        mockMvc.perform(get("/api/v1/orders/find-by-service-id")
                        .param("serviceId", id)
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(orderEntity.getId()));
    }

    @Test
    void chooseExpert_ShouldReturnConfirmation() throws Exception {
        String id = String.valueOf(save1.getId());
        mockMvc.perform(put("/api/v1/orders/choose-expert")
                        .param("suggestionId", id)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Expert chosen"));
    }

    @Test
    void searchOrders_ShouldReturnPagedSummary() throws Exception {

        mockMvc.perform(get("/api/v1/orders/search-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OrderSearchRequest()))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByExpert_ShouldReturnPagedSummary() throws Exception {

        mockMvc.perform(get("/api/v1/orders/find-all-by-expert")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk());
    }

    @Test
    void findOrderWithDetails_ShouldReturnOrderFindResponse() throws Exception {
        String id = String.valueOf(orderEntity.getId());
        orderEntity.setExpert(expertTest);
        orderService.save(orderEntity);
        save1.setExpert(expertTest);
        save1.setAccepted(true);
        suggestionService.save(save1);
        mockMvc.perform(get("/api/v1/orders/find-order-with-details")
                        .param("orderId", id)
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderEntity.getId()));
    }

    @Test
    void findOrderWithDetails_ForbiddenIfNotAccepted() throws Exception {
        String id = String.valueOf(orderEntity.getId());

        mockMvc.perform(get("/api/v1/orders/find-order-with-details")
                        .param("orderId", id)
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrderWithDetailsForAdmin_ShouldReturnOrderFindResponse() throws Exception {
        String id = String.valueOf(orderEntity.getId());

        mockMvc.perform(get("/api/v1/orders/find-order-with-details-for-admin-and-customer")
                        .param("orderId", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderEntity.getId()));
    }

    @Test
    void findAllByCustomer_ShouldReturnPagedOrders() throws Exception {

        mockMvc.perform(get("/api/v1/orders/find-all-by-customer")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(orderEntity.getId()));
    }

    @Test
    void updateStatusToStarted_ShouldReturnOrderFindResponse() throws Exception {

        String id = String.valueOf(orderEntity.getId());
        mockMvc.perform(put("/api/v1/orders/update-status-to-started")
                        .param("orderId", id)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderEntity.getId()));
    }

    @Test
    void updateStatusToDone_ShouldReturnOrderFindResponse() throws Exception {


        String id = String.valueOf(orderEntity.getId());
        mockMvc.perform(put("/api/v1/orders/update-status-to-done")
                        .param("orderId", id)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderEntity.getId()));
    }
}
