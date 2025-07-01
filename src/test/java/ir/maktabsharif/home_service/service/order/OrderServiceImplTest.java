package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
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


    @Test
    void saveWithDTO_shouldMapAndSaveOrder() {
        OrderSaveUpdateRequest dto = new OrderSaveUpdateRequest();
        dto.setCustomerId(1);
        dto.setExpertId(2);
        dto.setServiceId(3);

        Order order = new Order();
        when(mapper.mapToEntity(dto)).thenReturn(order);
        when(customerService.findById(dto.getCustomerId())).thenReturn(new Customer());
        when(expertService.findById(dto.getExpertId())).thenReturn(new Expert());
        when(serviceService.findById(dto.getServiceId())).thenReturn(new Service());

        service.saveWithDTO(dto);

        assertEquals(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION, order.getOrderStatus());
        assertNotNull(order.getCreationDate());
        assertNotNull(order.getCustomer());
        assertNotNull(order.getExpert());
        assertNotNull(order.getService());

        verify(repository).beginTransaction();
        verify(repository).save(order);
        verify(repository).commitTransaction();
    }

    @Test
    void updateWithDTO_shouldMapAndUpdateOrder() {
        OrderSaveUpdateRequest dto = new OrderSaveUpdateRequest();
        Order order = new Order();

        when(mapper.mapToEntity(dto)).thenReturn(order);

        service.updateWithDTO(dto);

        verify(repository).beginTransaction();
        verify(repository).update(order);
        verify(repository).commitTransaction();
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
        order.setExpert(null);

        Suggestion suggestion = new Suggestion();
        suggestion.setExpert(expert);
        Order orderRef = new Order();
        orderRef.setId(orderId);
        suggestion.setOrder(orderRef);

        when(suggestionService.findById(suggestionId)).thenReturn(suggestion);
        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        service.chooseExpert(suggestionId);

        assertEquals(expert, order.getExpert());
        assertEquals(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, order.getOrderStatus());

        verify(repository, times(2)).beginTransaction();
        verify(repository, times(2)).update(order);
        verify(repository, times(2)).commitTransaction();

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
        Expert expert = new Expert();
        List<OrderStatus> statuses = List.of(OrderStatus.STARTED);

        when(repository.existsBySpecialistAndOrderStatusIn(expert, statuses)).thenReturn(true);

        assertTrue(service.existsBySpecialistAndOrderStatusIn(expert, statuses));
    }

    @Test
    void existsBySpecialistAndOrderStatusIn_shouldReturnFalseFromRepo() {
        Expert expert = new Expert();
        List<OrderStatus> statuses = List.of(OrderStatus.STARTED);

        when(repository.existsBySpecialistAndOrderStatusIn(expert, statuses)).thenReturn(false);

        assertFalse(service.existsBySpecialistAndOrderStatusIn(expert, statuses));
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
}
