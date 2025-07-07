package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceImplTest {

    @Mock
    private SuggestionRepository repository;

    @Mock
    private SuggestionMapper mapper;

    @Mock
    private OrderService orderService;

    @Mock
    private ExpertService expertService;

    @InjectMocks
    private SuggestionServiceImpl service;

    @Test
    void registerSuggestion_validRequest_shouldSaveSuggestionAndUpdateOrderStatus() {
        Integer orderId = 1;
        Integer expertId = 2;
        double basePrice = 100.0;
        double suggestedPrice = 150.0;

        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest();
        request.setOrderId(orderId);
        request.setExpertId(expertId);
        request.setPrice(suggestedPrice);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);

        Service service2 = new Service();
        service2.setBasePrice(basePrice);
        order.setService(service2);

        Expert expert = new Expert();
        expert.setId(expertId);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setExpert(expert);
        suggestion.setPrice(suggestedPrice);

        Suggestion savedSuggestion = new Suggestion();
        savedSuggestion.setId(10);

        when(orderService.findById(orderId)).thenReturn(order);
        when(expertService.findById(expertId)).thenReturn(expert);
        when(mapper.mapToEntity(request)).thenReturn(suggestion);
        when(repository.save(suggestion)).thenReturn(savedSuggestion);

        Suggestion result = service.registerSuggestionForOrder(request);

        assertEquals(savedSuggestion, result);
        assertEquals(OrderStatus.WAITING_TO_CHOOSE_EXPERT, order.getOrderStatus());

        verify(orderService).save(order);
        verify(repository).save(suggestion);
    }

    @Test
    void registerSuggestion_invalidOrderStatus_shouldThrowException() {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest();
        request.setOrderId(1);
        request.setExpertId(2);
        request.setPrice(200.0);

        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);

        Service service1 = new Service();
        service1.setBasePrice(100.0);
        order.setService(service1);

        Expert expert = new Expert();

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setExpert(expert);
        suggestion.setPrice(200.0);

        when(orderService.findById(anyInt())).thenReturn(order);
        when(expertService.findById(anyInt())).thenReturn(expert);
        when(mapper.mapToEntity(request)).thenReturn(suggestion);

        InvalidRequestException ex = assertThrows(
                InvalidRequestException.class,
                () -> service.registerSuggestionForOrder(request)
        );

        assertEquals("Order is not waiting for any suggestions.", ex.getMessage());
    }

    @Test
    void updateWithDTO_shouldMapAndUpdate() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();
        dto.setId(1);

        Suggestion existing = new Suggestion();

        when(repository.findById(1)).thenReturn(Optional.of(existing));
        doNothing().when(mapper).updateEntityWithDTO(dto, existing);

        service.updateWithDTO(dto);

        verify(repository).save(any(Suggestion.class));
    }


    @Test
    void findAllByExpertId_shouldThrow_whenNoSuggestionsFound() {
        when(repository.findAllByExpertId(1)).thenReturn(Collections.emptyList());

        assertThrows(NoElementFoundException.class, () -> service.findAllByExpertId(1));
    }

    @Test
    void findAllByExpertId_shouldReturnList_whenSuggestionsExist() {
        Suggestion suggestion1 = new Suggestion();
        Suggestion suggestion2 = new Suggestion();

        SuggestionFindResponse response1 = new SuggestionFindResponse();
        SuggestionFindResponse response2 = new SuggestionFindResponse();

        when(repository.findAllByExpertId(1)).thenReturn(List.of(suggestion1, suggestion2));
        when(mapper.mapToResponse(suggestion1)).thenReturn(response1);
        when(mapper.mapToResponse(suggestion2)).thenReturn(response2);

        List<SuggestionFindResponse> result = service.findAllByExpertId(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
    }


    @Test
    void confirmSuggestionAcceptance_shouldSetAcceptedAndUpdate() {
        Suggestion suggestion = new Suggestion();
        suggestion.setAccepted(false);

        when(repository.findById(1)).thenReturn(Optional.of(suggestion));

        service.confirmSuggestionAcceptance(1);

        assertTrue(suggestion.getAccepted());
        verify(repository).save(suggestion);
    }

    @Test
    void findAllAndSortByPriceAsc_ShouldReturnSortedSuggestions() {
        Order order = new Order();
        order.setId(1);

        List<Suggestion> suggestions = List.of(
                new Suggestion(), new Suggestion(), new Suggestion()
        );

        when(orderService.findById(1)).thenReturn(order);
        when(repository.findAllByOrderAndSortByPriceAsc(order)).thenReturn(suggestions);

        List<Suggestion> result = service.findAllAndSortByPriceAsc(1);

        assertEquals(suggestions, result);
        verify(repository).findAllByOrderAndSortByPriceAsc(order);
    }

    @Test
    void findAllByAndSortByExpertScoreDesc_ShouldReturnSortedSuggestions() {
        Order order = new Order();
        order.setId(1);

        List<Suggestion> suggestions = List.of(
                new Suggestion(), new Suggestion(), new Suggestion()
        );

        when(orderService.findById(1)).thenReturn(order);
        when(repository.findAllByOrderAndSortByExpertScoreDesc(order)).thenReturn(suggestions);

        List<Suggestion> result = service.findAllByAndSortByExpertScoreDesc(1);

        assertEquals(suggestions, result);
        verify(repository).findAllByOrderAndSortByExpertScoreDesc(order);
    }

}
