package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.order.OrderService;
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
class SuggestionServiceImplTest {

    @Mock SuggestionRepository repository;
    @Mock SuggestionMapper mapper;
    @Mock ExpertService expertService;
    @Mock OrderService orderService;
    @Mock ExpertServiceService expertServiceService;
    @InjectMocks SuggestionServiceImpl service;

    Suggestion suggestion;
    Order order;
    Expert expert;
    UserSessionDTO session;

    @BeforeEach
    void setup() {
        suggestion = new Suggestion();
        suggestion.setId(1);
        suggestion.setPrice(2000d);
        order = new Order();
        order.setId(2);
        expert = new Expert();
        expert.setId(3);
        session = new UserSessionDTO();
        session.setUserId(3);
    }

    @Test
    void registerSuggestion_ShouldSave_WhenValid() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();
        dto.setOrderId(2);
        dto.setPrice(200.0);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(new ir.maktabsharif.home_service.model.service.Service());
        order.getService().setBasePrice(100.0);

        when(mapper.mapToEntity(dto)).thenReturn(suggestion);
        when(orderService.findById(2)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(3, order.getService().getId())).thenReturn(true);
        when(expertService.findById(3)).thenReturn(expert);
        when(repository.save(any())).thenReturn(suggestion);
        when(orderService.save(any())).thenReturn(order);

        Suggestion result = service.registerSuggestionForOrder(dto, session);
        assertNotNull(result);
        verify(repository).save(any());
    }

    @Test
    void registerSuggestion_ShouldThrow_WhenNotRegisteredExpertService() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();
        dto.setOrderId(2);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(new ir.maktabsharif.home_service.model.service.Service());

        when(mapper.mapToEntity(dto)).thenReturn(suggestion);
        when(orderService.findById(2)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(3, order.getService().getId())).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> service.registerSuggestionForOrder(dto, session));
    }

    @Test
    void registerSuggestion_ShouldThrow_WhenOrderStatusInvalid() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();
        dto.setOrderId(2);
        order.setOrderStatus(OrderStatus.STARTED);
        order.setService(new ir.maktabsharif.home_service.model.service.Service());
        order.getService().setBasePrice(100.0);

        when(mapper.mapToEntity(dto)).thenReturn(suggestion);
        when(orderService.findById(2)).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(3, order.getService().getId())).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> service.registerSuggestionForOrder(dto, session));
    }

    @Test
    void updateWithDTO_ShouldUpdate_WhenValid() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();
        dto.setId(1);
        order.setService(new ir.maktabsharif.home_service.model.service.Service());

        suggestion.setOrder(order);
        when(repository.findById(1)).thenReturn(Optional.of(suggestion));
        when(expertServiceService.existsByExpertIdAndServiceId(3, order.getService().getId())).thenReturn(true);
        when(expertService.findById(3)).thenReturn(expert);
        when(repository.save(suggestion)).thenReturn(suggestion);

        Suggestion result = service.updateWithDTO(dto, session);
        assertEquals(suggestion, result);
    }

    @Test
    void findAllAndSortByPriceAsc_ShouldReturnList() {
        when(orderService.findById(2)).thenReturn(order);
        when(repository.findAllByOrderAndSortByPriceAsc(order)).thenReturn(List.of(suggestion));

        List<Suggestion> result = service.findAllAndSortByPriceAsc(2);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByAndSortByExpertScoreDesc_ShouldThrow_WhenEmpty() {
        when(orderService.findById(2)).thenReturn(order);
        when(repository.findAllByOrderAndSortByExpertScoreDesc(order)).thenReturn(List.of());

        assertThrows(NoElementFoundException.class, () -> service.findAllByAndSortByExpertScoreDesc(2));
    }

    @Test
    void existsByOrderIdAndExpertIdAndAcceptedTrue_ShouldReturnTrue() {
        when(repository.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 3)).thenReturn(true);
        assertTrue(service.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 3));
    }

    @Test
    void findAllByExpertId_ShouldReturnList() {
        when(repository.findAllByExpertId(3)).thenReturn(List.of(suggestion));
        when(mapper.mapToResponse(suggestion)).thenReturn(new SuggestionFindResponse());

        List<SuggestionFindResponse> result = service.findAllByExpertId(3);
        assertEquals(1, result.size());
    }

    @Test
    void confirmSuggestionAcceptance_ShouldUpdateAcceptedTrue() {
        when(repository.findById(1)).thenReturn(Optional.of(suggestion));

        service.confirmSuggestionAcceptance(1);

        assertTrue(suggestion.getAccepted());
        verify(repository).save(suggestion);
    }
}
