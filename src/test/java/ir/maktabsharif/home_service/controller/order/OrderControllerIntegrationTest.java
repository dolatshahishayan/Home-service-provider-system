package ir.maktabsharif.home_service.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.util.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    private String adminToken;
    private String customerToken;
    private String expertToken;
    private Order orderEntity;
    private OrderFindResponse orderFindResponse;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_EXPERT);
        userService.save(user);
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

        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setPassword(passwordEncoder.encode("test"));
        user3.setIsEmailVerified(true);
        user3.setRole(Role.ROLE_CUSTOMER);
        userService.save(user3);
        UserDetails userDetails3 = userService.loadUserByUsername(user3.getEmail());
        customerToken = jwtUtil.generateToken(userDetails3);

        Customer customer = new Customer();
        customer.setId(1);

        Service service = new Service();
        service.setId(1);

        Expert expert = new Expert();
        expert.setId(1);

        orderEntity = new Order();
        orderEntity.setId(1);
        orderEntity.setDescription("Test Order");
        orderEntity.setProposedPrice(BigDecimal.valueOf(100.0));
        orderEntity.setStartDate(LocalDateTime.now());
        orderEntity.setAddress("Test Address");
        orderEntity.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        orderEntity.setCustomer(customer);
        orderEntity.setService(service);
        orderEntity.setExpert(expert);
        orderEntity.setCreationDate(LocalDateTime.now());
        orderEntity.setFinalPrice(BigDecimal.valueOf(120.0));

        orderFindResponse = new OrderFindResponse(
                1,
                "Test Order",
                100.0,
                LocalDateTime.now(),
                "Test Address",
                OrderStatus.WAITING_FOR_EXPERT_SUGGESTION,
                1,
                1,
                1,
                LocalDateTime.now(),
                120.0
        );
    }

    @AfterAll
    void deleteUsers() {
        userService.deleteAll();
    }

    @Test
    void saveOrder_ShouldReturnOrderFindResponse() throws Exception {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setDescription("Test Order");

        Mockito.when(orderService.saveWithDTO(any(OrderSaveUpdateRequest.class), any()))
                .thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class)))
                .thenReturn(orderFindResponse);

        mockMvc.perform(post("/api/v1/orders/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"));
    }

    @Test
    void updateOrder_ShouldReturnOrderFindResponse() throws Exception {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setDescription("Updated Order");

        Mockito.when(orderService.updateWithDTO(any(OrderSaveUpdateRequest.class), any()))
                .thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class)))
                .thenReturn(orderFindResponse);

        mockMvc.perform(put("/api/v1/orders/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"));
    }

    @Test
    void existsByExpertAndOrderStatusIn_ShouldReturnBoolean() throws Exception {
        Mockito.when(orderService.existsBySpecialistAndOrderStatusIn(eq(1), anyList()))
                .thenReturn(true);

        mockMvc.perform(get("/api/v1/orders/exists-by-expert-and-order-status-in")
                        .param("expertId", "1")
                        .param("orderStatuses", "WAITING_FOR_EXPERT_SUGGESTION")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByServiceId_ShouldReturnPagedOrders() throws Exception {
        Page<Order> page = new PageImpl<>(List.of(orderEntity));
        Mockito.when(orderService.findByServiceId(eq(1), any(PageRequest.class))).thenReturn(page);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-by-service-id")
                        .param("serviceId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void chooseExpert_ShouldReturnConfirmation() throws Exception {
        Mockito.doNothing().when(orderService).chooseExpert(eq(1));

        mockMvc.perform(put("/api/v1/orders/choose-expert")
                        .param("suggestionId", "1")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Expert chosen"));
    }

    @Test
    void searchOrders_ShouldReturnPagedSummary() throws Exception {
        Page<OrderSummaryDTO> page = new PageImpl<>(List.of(new OrderSummaryDTO()));
        Mockito.when(orderService.searchOrders(any(OrderSearchRequest.class), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/orders/search-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OrderSearchRequest()))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByExpert_ShouldReturnPagedSummary() throws Exception {
        Page<OrderSummaryDTO> page = new PageImpl<>(List.of(new OrderSummaryDTO()));
        Mockito.when(orderService.findAllByExpertId(any(PageRequest.class), eq(1)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/orders/find-all-by-expert")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk());
    }

    @Test
    void findOrderWithDetails_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(any(), any())).thenReturn(true);
        Mockito.when(orderService.findById(eq(1))).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-order-with-details")
                        .param("orderId", "1")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findOrderWithDetails_ForbiddenIfNotAccepted() throws Exception {
        Mockito.when(orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(eq(1), eq(1))).thenReturn(false);

        mockMvc.perform(get("/api/v1/orders/find-order-with-details")
                        .param("orderId", "1")
                        .header("Authorization", "Bearer " + expertToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrderWithDetailsForAdmin_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.findById(eq(1))).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-order-with-details-for-admin-and-customer")
                        .param("orderId", "1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findAllByCustomer_ShouldReturnPagedOrders() throws Exception {
        Page<Order> page = new PageImpl<>(List.of(orderEntity));
        Mockito.when(orderService.findByCustomerId(any(), any(PageRequest.class), any()))
                .thenReturn(page);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-all-by-customer")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void updateStatusToStarted_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.updateStatusToStarted(any(), any())).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(put("/api/v1/orders/update-status-to-started")
                        .param("orderId", "1")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateStatusToDone_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.updateStatusToDone(any(), any())).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(put("/api/v1/orders/update-status-to-done")
                        .param("orderId", "1")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
