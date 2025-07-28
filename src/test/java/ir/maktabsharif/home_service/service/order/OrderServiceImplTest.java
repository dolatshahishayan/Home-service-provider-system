package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.order.OrderRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private SuggestionService suggestionService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    @Mock
    private ServiceService serviceService;
    @Mock
    private UserService userService;

    @Test
    void testSaveWithDTO() {
        OrderSaveUpdateRequest dto = new OrderSaveUpdateRequest();
        dto.setServiceId(1);
        dto.setProposedPrice(200d);
        User user = new User();
        user.setId(10);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setBasePrice(100d);

        Order order = new Order();
        order.setProposedPrice(200d);
        when(userService.findById(10)).thenReturn(user);
        when(customerService.findById(10)).thenReturn(new Customer());
        when(serviceService.findById(1)).thenReturn(service);
        when(orderMapper.mapToEntity(dto)).thenReturn(order);
        when(orderRepository.save(any())).thenReturn(order);

        Order saved = orderService.saveWithDTO(dto, 10);
        assertEquals(order, saved);
    }

    @Test
    void testChooseExpert() {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(1);
        suggestion.setPrice(300d);
        Expert expert = new Expert();
        expert.setId(1);
        suggestion.setExpert(expert);
        Order order = new Order();
        order.setId(1);
        suggestion.setOrder(order);
        when(suggestionService.findById(1)).thenReturn(suggestion);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.chooseExpert(1);
        verify(suggestionService).confirmSuggestionAcceptance(1);
    }

    @Test
    void testFindAllByExpertId_Verified() {
        User user = new User();
        user.setId(1);
        Expert expert = new Expert();
        expert.setId(1);
        expert.setExpertStatus(ExpertStatus.VERIFIED);

        Page<Order> orders = new PageImpl<>(List.of(new Order()));
        when(userService.findById(1)).thenReturn(user);
        when(expertService.findById(1)).thenReturn(expert);
        when(orderRepository.findByExpertId(1, Pageable.unpaged())).thenReturn(orders);
        when(orderMapper.mapToSummary(any())).thenReturn(new OrderSummaryDTO());

        Page<OrderSummaryDTO> result = orderService.findAllByExpertId(Pageable.unpaged(), 1);
        assertFalse(result.isEmpty());
    }

    @Test
    void testExistsByOrderIdAndExpertIdAndAcceptedTrue() {
        User user = new User();
        user.setId(2);
        when(userService.findById(2)).thenReturn(user);
        when(suggestionService.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 2)).thenReturn(true);

        boolean exists = orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 2);
        assertTrue(exists);
    }

    @Test
    void testUpdateStatusToStarted() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setId(3);

        Order order = new Order();
        Customer customer = new Customer();
        customer.setEmail("test@mail.com");
        order.setCustomer(customer);
        order.setStartDate(LocalDateTime.now().minusHours(1));
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT);

        when(userService.findById(3)).thenReturn(user);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order updated = orderService.updateStatusToStarted(1, 3);
        assertEquals(OrderStatus.STARTED, updated.getOrderStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSearchOrders() {
        OrderSearchRequest request = new OrderSearchRequest();
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.mapToSummary(any())).thenReturn(new OrderSummaryDTO());

        Page<OrderSummaryDTO> result = orderService.searchOrders(request, Pageable.unpaged());
        assertFalse(result.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindByCustomerId() {
        User user = new User();
        user.setId(4);

        Order order = new Order();
        Page<Order> page = new PageImpl<>(List.of(order));
        when(userService.findById(4)).thenReturn(user);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Order> result = orderService.findByCustomerId(null, Pageable.unpaged(), 4);
        assertFalse(result.isEmpty());
    }

    @Test
    void testExistsBySpecialistAndOrderStatusIn() {
        Expert expert = new Expert();
        when(expertService.findById(1)).thenReturn(expert);
        when(orderRepository.existsByExpertAndOrderStatusIn(any(), any())).thenReturn(true);

        boolean result = orderService.existsBySpecialistAndOrderStatusIn(1, List.of(OrderStatus.STARTED));
        assertTrue(result);
    }

    @Test
    void testFindByServiceId() {
        Page<Order> orders = new PageImpl<>(List.of(new Order()));
        when(orderRepository.findByServiceId(1, Pageable.unpaged())).thenReturn(orders);

        Page<Order> result = orderService.findByServiceId(1, Pageable.unpaged());
        assertFalse(result.isEmpty());
    }

    @Test
    void updateWithDTO_shouldUpdateOrderWithExpert() {
        OrderSaveUpdateRequest dto = new OrderSaveUpdateRequest();
        dto.setId(1);
        dto.setExpertId(2);

        User principal = new User();
        principal.setId(4);

        Order order = new Order();
        order.setId(1);

        Expert expert = new Expert();
        expert.setId(2);

        Customer customer = new Customer();
        customer.setId(4);

        when(userService.findById(4)).thenReturn(principal);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        doNothing().when(orderMapper).updateEntityWithDTO(dto, order);
        when(customerService.findById(4)).thenReturn(customer);
        when(expertService.findById(2)).thenReturn(expert);
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.updateWithDTO(dto, 4);

        assertEquals(customer, result.getCustomer());
        assertEquals(expert, result.getExpert());
        verify(orderMapper).updateEntityWithDTO(dto, order);
    }

    @Test
    void updateStatusToDone_shouldUpdateStatusToDone() {
        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.DONE);

        User user = new User();
        user.setId(5);
        user.setEmail("test@mail.com");
        Customer customer = new Customer();
        customer.setEmail("test@mail.com");
        order.setCustomer(customer);
        when(userService.findById(5)).thenReturn(user);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.updateStatusToDone(1, 5);

        assertEquals(OrderStatus.DONE, result.getOrderStatus());
    }

    @Test
    void reduce1ScoreFromExpertPerHour_shouldReturnHoursDifference() {
        Suggestion suggestion = new Suggestion();
        suggestion.setStartDate(LocalDateTime.now().minusHours(3));

        long hours = orderService.reduce1ScoreFromExpertPerHour(suggestion);

        assertEquals(3, hours);
    }

    @Test
    void reduce1ScoreFromExpertPerHour_shouldThrowIfStartDateIsNotBeforeNow() {
        Suggestion suggestion = new Suggestion();
        suggestion.setStartDate(LocalDateTime.now().plusHours(2));

        assertThrows(CouldNotUpdateException.class, () ->
                orderService.reduce1ScoreFromExpertPerHour(suggestion));
    }

}

