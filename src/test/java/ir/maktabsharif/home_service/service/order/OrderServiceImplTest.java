package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.order.OrderRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private SuggestionService suggestionService;

    @Mock
    private ExpertServiceService expertServiceService;

    @Mock
    private CustomerService customerService;

    @Mock
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private OrderServiceImpl service;

    private final Customer test = new Customer();

    @BeforeEach
    void setUp() {
        test.setEmail("user@example.com");
    }

    @Test
    void saveWithDTO_shouldMapAndSaveOrder_whenPriceIsValid() {
        OrderSaveUpdateRequest dto = new OrderSaveUpdateRequest();
        dto.setExpertId(2);
        dto.setServiceId(3);
        dto.setProposedPrice(2000d);
        Integer customerId = 1;
        Customer customer = new Customer();
        Expert expert = new Expert();
        Service service2 = new Service();
        service2.setBasePrice(1500d);

        Order mappedOrder = new Order();
        mappedOrder.setProposedPrice(dto.getProposedPrice());

        when(mapper.mapToEntity(dto)).thenReturn(mappedOrder);
        when(customerService.findById(customerId)).thenReturn(customer);
        when(expertService.findById(dto.getExpertId())).thenReturn(expert);
        when(serviceService.findById(dto.getServiceId())).thenReturn(service2);
        when(repository.save(mappedOrder)).thenReturn(mappedOrder);

        Order result = service.saveWithDTO(dto, customerId);

        assertEquals(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION, result.getOrderStatus());
        assertNotNull(result.getCreationDate());
        assertEquals(customer, result.getCustomer());
        assertEquals(expert, result.getExpert());
        assertEquals(service2, result.getService());
        verify(repository).save(mappedOrder);
    }


    @Test
    void updateWithDTO_shouldUpdateOrderCorrectly() {
        OrderSaveUpdateRequest dto = new OrderSaveUpdateRequest();
        dto.setId(1);
        dto.setExpertId(20);
        dto.setServiceId(30);

        Order existingOrder = new Order();
        existingOrder.setId(dto.getId());
        Integer customerId = 1;
        Customer customer = new Customer();
        Expert expert = new Expert();
        Service service2 = new Service();

        when(repository.findById(dto.getId())).thenReturn(Optional.of(existingOrder));
        when(customerService.findById(customerId)).thenReturn(customer);
        when(expertService.findById(dto.getExpertId())).thenReturn(expert);
        when(serviceService.findById(dto.getServiceId())).thenReturn(service2);

        doNothing().when(mapper).updateEntityWithDTO(dto, existingOrder);

        when(repository.save(existingOrder)).thenReturn(existingOrder);

        Order updatedOrder = service.updateWithDTO(dto,customerId);

        assertSame(existingOrder, updatedOrder);
        verify(mapper).updateEntityWithDTO(dto, existingOrder);
        verify(repository).save(existingOrder);

        assertEquals(customer, existingOrder.getCustomer());
        assertEquals(expert, existingOrder.getExpert());
        assertEquals(service2, existingOrder.getService());
    }


    @Test
    void chooseExpert_shouldThrowIfExpertAlreadySet() {
        Integer suggestionId = 1;
        Integer orderId = 10;

        Expert expert = new Expert();
        Order order = new Order();
        order.setId(orderId);
        order.setExpert(new Expert());

        Suggestion suggestion = new Suggestion();
        suggestion.setExpert(expert);
        Order orderRef = new Order();
        orderRef.setId(orderId);
        suggestion.setOrder(orderRef);

        when(suggestionService.findById(suggestionId)).thenReturn(suggestion);
        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(CouldNotUpdateException.class, () -> service.chooseExpert(suggestionId));
    }

    @Test
    void chooseExpert_shouldUpdateOrderAndConfirmSuggestion() {
        Integer suggestionId = 1;
        Integer orderId = 10;

        Expert expert = new Expert();

        Order order = new Order();
        order.setId(orderId);

        Suggestion suggestion = new Suggestion();
        suggestion.setExpert(expert);

        Order orderRef = new Order();
        orderRef.setId(orderId);
        suggestion.setOrder(orderRef);

        when(suggestionService.findById(suggestionId)).thenReturn(suggestion);
        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        service.chooseExpert(suggestionId);

        assertEquals(expert, order.getExpert());
        assertEquals(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, order.getOrderStatus());

        verify(repository, times(2)).save(order);
        verify(suggestionService).confirmSuggestionAcceptance(suggestionId);
    }


    @Test
    void findAllByExpertId_shouldReturnMappedOrders() {
        Integer expertId = 10;

        ExpertService es = new ExpertService();
        Service serv = new Service();
        serv.setId(100);
        es.setService(serv);

        Order order1 = new Order();
        Order order2 = new Order();

        OrderFindResponse response1 = new OrderFindResponse();
        OrderFindResponse response2 = new OrderFindResponse();

        when(expertServiceService.findByExpertId(expertId)).thenReturn(List.of(es));
        when(repository.findByServiceId(100)).thenReturn(List.of(order1, order2));
        when(mapper.mapToResponse(order1)).thenReturn(response1);
        when(mapper.mapToResponse(order2)).thenReturn(response2);

        List<OrderFindResponse> result = service.findAllByExpertId(expertId);

        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
    }


    @Test
    void existsBySpecialistAndOrderStatusIn_shouldReturnTrueFromRepo() {
        Integer expertId = 10;
        Expert expert = new Expert();
        List<OrderStatus> statuses = List.of(OrderStatus.STARTED);

        when(expertService.findById(expertId)).thenReturn(expert);
        when(repository.existsByExpertAndOrderStatusIn(expert, statuses)).thenReturn(true);

        assertTrue(service.existsBySpecialistAndOrderStatusIn(expertId, statuses));
    }


    @Test
    void existsBySpecialistAndOrderStatusIn_shouldReturnFalseFromRepo() {
        Integer expertId = 20;
        Expert expert = new Expert();
        List<OrderStatus> statuses = List.of(OrderStatus.STARTED);

        when(expertService.findById(expertId)).thenReturn(expert);
        when(repository.existsByExpertAndOrderStatusIn(expert, statuses)).thenReturn(false);

        assertFalse(service.existsBySpecialistAndOrderStatusIn(expertId, statuses));
    }


    @Test
    void findByServiceId_shouldReturnListFromRepo() {
        Integer serviceId = 3;
        Order order = new Order();

        when(repository.findByServiceId(serviceId)).thenReturn(List.of(order));

        List<Order> result = service.findByServiceId(serviceId);

        assertEquals(1, result.size());
        assertEquals(order, result.getFirst());
    }

    @Test
    void updateStatusToStarted_ShouldUpdate_WhenCurrentUserIsCustomer() {
        Integer orderId = 1;

        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setEmail("user@example.com");

        Customer customer = new Customer();
        customer.setEmail("user@example.com");

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT);
        order.setCustomer(customer);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        Order result = service.updateStatusToStarted(orderId, currentUser);

        assertEquals(OrderStatus.STARTED, result.getOrderStatus());
        verify(repository).save(order);
    }

    @Test
    void updateStatusToDone_ShouldUpdate_WhenCurrentUserIsCustomer() {
        Integer orderId = 1;

        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setEmail("user@example.com");

        Customer customer = new Customer();
        customer.setEmail("user@example.com");

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.STARTED);
        order.setCustomer(customer);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        Order result = service.updateStatusToDone(orderId, currentUser);

        assertEquals(OrderStatus.DONE, result.getOrderStatus());
        verify(repository).save(order);
    }


}
