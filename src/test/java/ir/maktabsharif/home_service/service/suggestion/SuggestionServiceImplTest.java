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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void saveWithDTO_shouldThrow_whenOrderStatusIsInvalid() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();

        Order order = new Order();
        order.setOrderStatus(OrderStatus.STARTED);

        Service serviceEntity = new Service();
        serviceEntity.setBasePrice(100.0);
        order.setService(serviceEntity);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(120.0);
        dto.setOrderId(1);
        dto.setExpertId(1);
        when(mapper.mapToEntity(dto)).thenReturn(suggestion);

        when(orderService.findById(anyInt())).thenReturn(order);
        when(expertService.findById(anyInt())).thenReturn(new ir.maktabsharif.home_service.model.user.Expert());

        assertThrows(InvalidRequestException.class, () -> service.saveWithDTO(dto));
    }

    @Test
    void saveWithDTO_shouldThrow_whenPriceBelowBasePrice() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);

        Service serviceEntity = new Service();
        serviceEntity.setBasePrice(150.0);
        order.setService(serviceEntity);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(120.0);
        dto.setOrderId(1);
        dto.setExpertId(1);
        when(mapper.mapToEntity(dto)).thenReturn(suggestion);

        when(orderService.findById(anyInt())).thenReturn(order);
        when(expertService.findById(anyInt())).thenReturn(new ir.maktabsharif.home_service.model.user.Expert());

        assertThrows(InvalidRequestException.class, () -> service.saveWithDTO(dto));
    }

    @Test
    void saveWithDTO_shouldSave_whenValid() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);

        Service serviceEntity = new Service();
        serviceEntity.setBasePrice(100.0);
        order.setService(serviceEntity);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(120.0);
        dto.setOrderId(1);
        dto.setExpertId(1);
        when(mapper.mapToEntity(dto)).thenReturn(suggestion);

        when(orderService.findById(anyInt())).thenReturn(order);
        when(expertService.findById(anyInt())).thenReturn(new ir.maktabsharif.home_service.model.user.Expert());

        service.saveWithDTO(dto);

        assertNotNull(suggestion.getCreationDate());
        verify(repository).beginTransaction();
        verify(repository).save(suggestion);
        verify(repository).commitTransaction();
    }


    @Test
    void updateWithDTO_shouldMapAndUpdate() {
        SuggestionSaveUpdateRequest dto = new SuggestionSaveUpdateRequest();
        Suggestion suggestion = new Suggestion();

        when(mapper.mapToEntity(dto)).thenReturn(suggestion);

        service.updateWithDTO(dto);

        verify(repository).beginTransaction();
        verify(repository).update(suggestion);
        verify(repository).commitTransaction();
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
        when(mapper.mapToDTO(suggestion1)).thenReturn(response1);
        when(mapper.mapToDTO(suggestion2)).thenReturn(response2);

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
        verify(repository).beginTransaction();
        verify(repository).update(suggestion);
        verify(repository).commitTransaction();
    }
}
