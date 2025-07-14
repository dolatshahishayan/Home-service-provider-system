package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.order.OrderRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository repository;
    @Mock private OrderMapper mapper;
    @Mock private SuggestionService suggestionService;
    @Mock private CustomerService customerService;
    @Mock private ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    @Mock private ServiceService serviceService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Expert expert;
    private Customer customer;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1);
        customer = new Customer();
        customer.setEmail("a@b.com");
        order.setCustomer(customer);
        order.setStartDate(LocalDateTime.now().minusHours(2));
        expert = new Expert();
        expert.setId(1);
        expert.setScore(5.0);
        order.setExpert(expert);
    }

    @Test
    void saveWithDTO_ShouldThrow_WhenCustomerIdIsNull() {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        assertThrows(InvalidRequestException.class, () -> orderService.saveWithDTO(request, null));
    }

    @Test
    void saveWithDTO_ShouldThrow_WhenPriceIsLessThanBase() {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setServiceId(1);
        request.setProposedPrice(50d);

        Service service = new Service();
        service.setBasePrice(100d);

        Order mappedOrder = new Order();
        mappedOrder.setProposedPrice(50d);

        when(serviceService.findById(1)).thenReturn(service);
        when(mapper.mapToEntity(request)).thenReturn(mappedOrder);

        Customer customer = new Customer();
        when(customerService.findById(1)).thenReturn(customer);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> orderService.saveWithDTO(request, 1));
        assertEquals("Proposed price must be greater than the service price", ex.getMessage());
    }

    @Test
    void chooseExpert_ShouldUpdateOrderAndConfirmSuggestion() {
        Suggestion suggestion = new Suggestion();
        Order order = new Order();
        order.setId(1);
        suggestion.setOrder(order);
        suggestion.setExpert(new Expert());
        suggestion.setPrice(123D);

        when(suggestionService.findById(any())).thenReturn(suggestion);
        when(repository.findById(any())).thenReturn(Optional.of(order));
        when(repository.save(any())).thenReturn(order);

        orderService.chooseExpert(1);
        verify(repository, times(2)).save(order);
        verify(suggestionService).confirmSuggestionAcceptance(1);
    }

    @Test
    void setExpertAndFinalPriceForOrder_ShouldThrow_WhenAlreadyAssigned() {
        Order o = new Order();
        o.setExpert(new Expert());
        Suggestion s = new Suggestion();
        s.setOrder(o);
        when(suggestionService.findById(1)).thenReturn(s);
        when(repository.findById(any())).thenReturn(Optional.of(o));

        assertThrows(CouldNotUpdateException.class, () -> orderService.chooseExpert(1));
    }

    @Test
    void updateStatusToStarted_ShouldThrow_WhenStartDateInFuture() {
        Order o = new Order();
        o.setStartDate(LocalDateTime.now().plusHours(1));
        o.setCustomer(customer);
        when(repository.findById(1)).thenReturn(Optional.of(o));
        assertThrows(CouldNotUpdateException.class, () -> orderService.updateStatusToStarted(1, new UserSessionDTO(customer.getId(),"a@b.com", Role.CUSTOMER)));
    }

    @Test
    void testUpdateStatusToDone_success() {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setEmail("a@b.com");

        when(repository.findById(anyInt())).thenReturn(Optional.of(order));
        when(orderService.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updatedOrder = orderService.updateStatusToDone(1, currentUser);

        assertEquals(OrderStatus.DONE, updatedOrder.getOrderStatus());
        verify(repository).save(order);
    }

    @Test
    void testUpdateStatusToDone_userNotOwner_shouldThrow() {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setEmail("test@example.com");

        when(repository.findById(anyInt())).thenReturn(Optional.of(order));

        assertThrows(CouldNotUpdateException.class, () ->
                orderService.updateStatusToDone(1, currentUser)
        );
    }


    @Test
    void testReduce1ScoreFromExpertPerHour_whenExpertScoreIsNull_shouldThrowInvalidRequestException() {
        Expert expert = new Expert();
        expert.setId(1);
        expert.setScore(null);

        Order order = new Order();
        order.setExpert(expert);
        order.setStartDate(LocalDateTime.now().minusHours(1));

        when(expertService.findById(1)).thenReturn(expert);

        assertThrows(InvalidRequestException.class, () ->
                orderService.reduce1ScoreFromExpertPerHour(order)
        );
    }

    @Test
    void testReduce1ScoreFromExpertPerHour_whenStartDateIsNotBeforeNow_shouldThrowCouldNotUpdateException() {
        Expert expert = new Expert();
        expert.setId(1);
        expert.setScore(4.5); // Valid score

        Order order = new Order();
        order.setExpert(expert);
        order.setStartDate(LocalDateTime.now().plusHours(1)); // Future date

        when(expertService.findById(1)).thenReturn(expert);

        assertThrows(CouldNotUpdateException.class, () ->
                orderService.reduce1ScoreFromExpertPerHour(order)
        );
    }

    @Test
    void testReduce1ScoreFromExpertPerHour_whenValid_shouldReturnCorrectHourDifference() {
        Expert expert = new Expert();
        expert.setId(1);
        expert.setScore(5.0); // Valid score

        LocalDateTime startDate = LocalDateTime.now().minusHours(3);

        Order order = new Order();
        order.setExpert(expert);
        order.setStartDate(startDate);

        when(expertService.findById(1)).thenReturn(expert);

        long result = orderService.reduce1ScoreFromExpertPerHour(order);

        assertEquals(3L, result); // 3 hours difference
    }

    @Test
    void updateWithDTO_ShouldUpdateOrderFields() {
        OrderSaveUpdateRequest request = new OrderSaveUpdateRequest();
        request.setId(1);
        request.setExpertId(2);
        request.setServiceId(5);
        request.setProposedPrice(200d);
        Expert e = new Expert();
        when(repository.findById(1)).thenReturn(Optional.of(order));
        when(customerService.findById(1)).thenReturn(customer);
        when(expertService.findById(2)).thenReturn(e);
        when(repository.save(any())).thenReturn(order);
        orderService.updateWithDTO(request, 1);
        verify(repository).save(order);
    }

    @Test
    void testFindAllByExpertId_shouldReturnOrderSummaryList() {
        SuggestionFindResponse suggestion1 = new SuggestionFindResponse();
        suggestion1.setOrderId(1);

        SuggestionFindResponse suggestion2 = new SuggestionFindResponse();
        suggestion2.setOrderId(2);

        Order order1 = new Order();
        order1.setId(1);

        Order order2 = new Order();
        order2.setId(2);

        OrderSummaryDTO summary1 = new OrderSummaryDTO();
        OrderSummaryDTO summary2 = new OrderSummaryDTO();

        when(suggestionService.findAllByExpertId(anyInt())).thenReturn(List.of(suggestion1, suggestion2));
        when(repository.findById(1)).thenReturn(Optional.of(order1));
        when(repository.findById(2)).thenReturn(Optional.of(order2));
        when(mapper.mapToSummary(order1)).thenReturn(summary1);
        when(mapper.mapToSummary(order2)).thenReturn(summary2);

        // Act
        List<OrderSummaryDTO> result = orderService.findAllByExpertId(1);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(summary1));
        assertTrue(result.contains(summary2));
        verify(suggestionService).findAllByExpertId(1);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).findById(2);
    }

    @Test
    void testFindAllByExpertId_whenNoSuggestions_shouldReturnEmptyList() {
        // Arrange
        when(suggestionService.findAllByExpertId(anyInt())).thenReturn(Collections.emptyList());

        // Act
        List<OrderSummaryDTO> result = orderService.findAllByExpertId(1);

        // Assert
        assertTrue(result.isEmpty());
        verify(suggestionService).findAllByExpertId(1);
        verifyNoInteractions(repository);
        verifyNoInteractions(mapper);
    }


    @Test
    void existsByOrderIdAndExpertIdAndAcceptedTrue_ShouldReturnTrue() {
        when(suggestionService.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 1)).thenReturn(true);
        assertTrue(orderService.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 1));
    }

    @Test
    void existsBySpecialistAndOrderStatusIn_ShouldReturnTrue() {
        List<OrderStatus> statuses = List.of(OrderStatus.STARTED);
        when(expertService.findById(1)).thenReturn(expert);
        when(repository.existsByExpertAndOrderStatusIn(expert, statuses)).thenReturn(true);
        assertTrue(orderService.existsBySpecialistAndOrderStatusIn(1, statuses));
    }

    @Test
    void findByServiceId_ShouldReturnOrders() {
        when(repository.findByServiceId(1)).thenReturn(List.of(new Order()));
        assertEquals(1, orderService.findByServiceId(1).size());
    }

    @Test
    void findByServiceId_ShouldThrow_WhenEmpty() {
        when(repository.findByServiceId(1)).thenReturn(List.of());
        assertThrows(NoElementFoundException.class, () -> orderService.findByServiceId(1));
    }

    @Test
    void findByCustomerId_ShouldReturnOrders() {
        List<Order> orders = List.of(new Order());
        when(repository.findByCustomerId(1)).thenReturn(orders);
        List<Order> result = orderService.findByCustomerId(1);
        assertEquals(1, result.size());
    }

    @Test
    void updateStatus_ShouldThrow_WhenWrongUser() {
        UserSessionDTO user = new UserSessionDTO();
        user.setEmail("notmatch@example.com");

        order.setStartDate(LocalDateTime.now().minusHours(1));

        when(repository.findById(1)).thenReturn(Optional.of(order));

        assertThrows(CouldNotUpdateException.class, () -> orderService.updateStatusToDone(1, user));
    }

}
