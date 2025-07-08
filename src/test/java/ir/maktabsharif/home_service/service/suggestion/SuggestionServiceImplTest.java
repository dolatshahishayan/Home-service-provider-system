package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceImplTest {

    @Mock private SuggestionRepository repository;
    @Mock private SuggestionMapper mapper;
    @Mock private ExpertService expertService;
    @Mock private OrderService orderService;
    @Mock private ExpertServiceService expertServiceService;

    @InjectMocks
    private SuggestionServiceImpl suggestionService;

    @Test
    void registerSuggestionForOrder_shouldSaveAndUpdateOrderStatus() {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest();
        request.setOrderId(1);
        request.setPrice(200.0);

        UserSessionDTO session = new UserSessionDTO();
        session.setUserId(10);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setId(5);
        service.setBasePrice(100.0);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(200.0);

        when(mapper.mapToEntity(request)).thenReturn(suggestion);
        when(orderService.findById(1)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(10, 5)).thenReturn(true);
        when(expertService.findById(10)).thenReturn(new Expert());
        when(repository.save(ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Suggestion result = suggestionService.registerSuggestionForOrder(request, session);

        assertThat(result.getOrder().getOrderStatus()).isEqualTo(OrderStatus.WAITING_TO_CHOOSE_EXPERT);
        verify(orderService).save(order);
        verify(repository).save(ArgumentMatchers.any());
    }

    @Test
    void registerSuggestionForOrder_shouldThrow_whenExpertNotInService() {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest();
        request.setOrderId(1);
        request.setPrice(200.0);

        UserSessionDTO session = new UserSessionDTO();
        session.setUserId(10);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setId(5);
        service.setBasePrice(100.0);

        Order order = new Order();
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(200.0);

        when(mapper.mapToEntity(request)).thenReturn(suggestion);
        when(orderService.findById(1)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(10, 5)).thenReturn(false);

        assertThatThrownBy(() -> suggestionService.registerSuggestionForOrder(request, session))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Expert Id and Service Id are not registered");
    }

    @Test
    void registerSuggestionForOrder_shouldThrow_whenOrderStatusInvalid() {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest();
        request.setOrderId(1);
        request.setPrice(200.0);

        UserSessionDTO session = new UserSessionDTO();
        session.setUserId(10);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setId(5);
        service.setBasePrice(100.0);

        Order order = new Order();
        order.setService(service);
        order.setOrderStatus(OrderStatus.DONE);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(200.0);

        when(mapper.mapToEntity(request)).thenReturn(suggestion);
        when(orderService.findById(1)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(10, 5)).thenReturn(true);

        assertThatThrownBy(() -> suggestionService.registerSuggestionForOrder(request, session))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Order is not waiting for any suggestions");
    }

    @Test
    void registerSuggestionForOrder_shouldThrow_whenPriceTooLow() {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest();
        request.setOrderId(1);
        request.setPrice(50.0);

        UserSessionDTO session = new UserSessionDTO();
        session.setUserId(10);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setId(5);
        service.setBasePrice(100.0);

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(50.0);

        when(mapper.mapToEntity(request)).thenReturn(suggestion);
        when(orderService.findById(1)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(10, 5)).thenReturn(true);

        assertThatThrownBy(() -> suggestionService.registerSuggestionForOrder(request, session))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Price must be greater than the base price");
    }
}
