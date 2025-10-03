package ir.maktabsharif.home_service.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
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
@Import(OrderControllerIntegrationTest.MockConfig.class)
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
    private SecurityContextUtil securityContextUtil;

    static class MockConfig {
        @Bean
        OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }

        @Bean
        OrderMapper orderMapper() {
            return Mockito.mock(OrderMapper.class);
        }

        @Bean
        SecurityContextUtil securityContextUtil() {
            return Mockito.mock(SecurityContextUtil.class);
        }
    }

    private Order orderEntity;
    private OrderFindResponse orderFindResponse;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(principal);

        Customer customer = new Customer();
        customer.setId(1);

        Service service = new Service();
        service.setId(1);

        Expert expert = new Expert();
        expert.setId(1);

        orderEntity = new Order();
        orderEntity.setId(1);
        orderEntity.setDescription("Test Order");
        orderEntity.setProposedPrice(100.0);
        orderEntity.setStartDate(LocalDateTime.now());
        orderEntity.setAddress("Test Address");
        orderEntity.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        orderEntity.setCustomer(customer);
        orderEntity.setService(service);
        orderEntity.setExpert(expert);
        orderEntity.setCreationDate(LocalDateTime.now());
        orderEntity.setFinalPrice(120.0);

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


    @Test
    void saveOrder_ShouldReturnOrderFindResponse() throws Exception {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setDescription("Test Order");

        Mockito.when(orderService.saveWithDTO(any(OrderSaveUpdateRequest.class), eq(1)))
                .thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class)))
                .thenReturn(orderFindResponse);

        mockMvc.perform(post("/api/v1/orders/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Order"));
    }

    @Test
    void updateOrder_ShouldReturnOrderFindResponse() throws Exception {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setDescription("Updated Order");

        Mockito.when(orderService.updateWithDTO(any(OrderSaveUpdateRequest.class), eq(1)))
                .thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class)))
                .thenReturn(orderFindResponse);

        mockMvc.perform(put("/api/v1/orders/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
                        .param("orderStatuses", "WAITING_FOR_EXPERT_SUGGESTION"))
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
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void chooseExpert_ShouldReturnConfirmation() throws Exception {
        Mockito.doNothing().when(orderService).chooseExpert(eq(1));

        mockMvc.perform(put("/api/v1/orders/choose-expert")
                        .param("suggestionId", "1"))
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
                        .content(objectMapper.writeValueAsString(new OrderSearchRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByExpert_ShouldReturnPagedSummary() throws Exception {
        Page<OrderSummaryDTO> page = new PageImpl<>(List.of(new OrderSummaryDTO()));
        Mockito.when(orderService.findAllByExpertId(any(PageRequest.class), eq(1)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/orders/find-all-by-expert")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findOrderWithDetails_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(eq(1), eq(1))).thenReturn(true);
        Mockito.when(orderService.findById(eq(1))).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-order-with-details")
                        .param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findOrderWithDetails_ForbiddenIfNotAccepted() throws Exception {
        Mockito.when(orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(eq(1), eq(1))).thenReturn(false);

        mockMvc.perform(get("/api/v1/orders/find-order-with-details")
                        .param("orderId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findOrderWithDetailsForAdmin_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.findById(eq(1))).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-order-with-details-for-admin-and-customer")
                        .param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findAllByCustomer_ShouldReturnPagedOrders() throws Exception {
        Page<Order> page = new PageImpl<>(List.of(orderEntity));
        Mockito.when(orderService.findByCustomerId(any(), any(PageRequest.class), eq(1)))
                .thenReturn(page);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(get("/api/v1/orders/find-all-by-customer")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void updateStatusToStarted_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.updateStatusToStarted(eq(1), eq(1))).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(put("/api/v1/orders/update-status-to-started")
                        .param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateStatusToDone_ShouldReturnOrderFindResponse() throws Exception {
        Mockito.when(orderService.updateStatusToDone(eq(1), eq(1))).thenReturn(orderEntity);
        Mockito.when(orderMapper.mapToResponse(any(Order.class))).thenReturn(orderFindResponse);

        mockMvc.perform(put("/api/v1/orders/update-status-to-done")
                        .param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
