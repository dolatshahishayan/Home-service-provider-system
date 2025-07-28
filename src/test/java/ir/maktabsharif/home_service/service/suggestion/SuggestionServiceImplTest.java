package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceImplTest {

    @InjectMocks
    SuggestionServiceImpl suggestionService;

    @Mock
    SuggestionRepository suggestionRepository;

    @Mock
    SuggestionMapper suggestionMapper;

    @Mock
    ExpertService expertService;

    @Mock
    OrderService orderService;

    @Mock
    ExpertServiceService expertServiceService;


    @Test
    void registerSuggestionForOrder_success() {
        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setOrderId(1);
        req.setPrice(150d);

        Expert expert = new Expert();
        expert.setId(10);
        expert.setExpertStatus(ExpertStatus.VERIFIED);

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.user()).thenReturn(expert);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setBasePrice(100d);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(150d);

        when(suggestionMapper.mapToEntity(req)).thenReturn(suggestion);
        when(expertService.findById(expert.getId())).thenReturn(expert);
        when(orderService.findById(req.getOrderId())).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(expert.getId(), service.getId())).thenReturn(true);
        when(suggestionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderService.save(any(Order.class))).thenReturn(order);

        Suggestion result = suggestionService.registerSuggestionForOrder(req, principal);

        assertNotNull(result);
        assertEquals(expert, result.getExpert());
        assertFalse(result.getAccepted());
        verify(orderService).save(order);
    }

    @Test
    void registerSuggestionForOrder_priceLessThanBasePrice_throws() {
        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setOrderId(1);
        req.setPrice(50d);

        Expert expert = new Expert();
        expert.setId(10);
        expert.setExpertStatus(ExpertStatus.VERIFIED);

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.user()).thenReturn(expert);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setBasePrice(100d);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(50d);

        when(suggestionMapper.mapToEntity(req)).thenReturn(suggestion);
        when(expertService.findById(expert.getId())).thenReturn(expert);
        when(orderService.findById(req.getOrderId())).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(expert.getId(), service.getId())).thenReturn(true);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> suggestionService.registerSuggestionForOrder(req, principal));
        assertEquals("Price must be greater than the base price.", ex.getMessage());
    }

    @Test
    void registerSuggestionForOrder_expertNotVerified_throws() {
        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setOrderId(1);
        req.setPrice(150d);

        Expert expert = new Expert();
        expert.setId(10);
        expert.setExpertStatus(ExpertStatus.NEW);

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.user()).thenReturn(expert);

        when(expertService.findById(expert.getId())).thenReturn(expert);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> suggestionService.registerSuggestionForOrder(req, principal));
        assertEquals("Expert status must be VERIFIED", ex.getMessage());
    }

    @Test
    void registerSuggestionForOrder_expertServiceNotExists_throws() {
        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setOrderId(1);
        req.setPrice(150d);

        Expert expert = new Expert();
        expert.setId(10);
        expert.setExpertStatus(ExpertStatus.VERIFIED);

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.user()).thenReturn(expert);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setBasePrice(100d);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(150d);

        when(suggestionMapper.mapToEntity(req)).thenReturn(suggestion);
        when(expertService.findById(expert.getId())).thenReturn(expert);
        when(orderService.findById(req.getOrderId())).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(expert.getId(), service.getId())).thenReturn(false);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> suggestionService.registerSuggestionForOrder(req, principal));
        assertEquals("Expert Id and Service Id are not registered", ex.getMessage());
    }

    @Test
    void registerSuggestionForOrder_orderStatusInvalid_throws() {
        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setOrderId(1);
        req.setPrice(150d);

        Expert expert = new Expert();
        expert.setId(10);
        expert.setExpertStatus(ExpertStatus.VERIFIED);

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.user()).thenReturn(expert);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setBasePrice(100d);

        Order order = new Order();
        order.setId(1);
        order.setOrderStatus(OrderStatus.DONE);
        order.setService(service);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);
        suggestion.setPrice(150d);

        when(suggestionMapper.mapToEntity(req)).thenReturn(suggestion);
        when(expertService.findById(expert.getId())).thenReturn(expert);
        when(orderService.findById(req.getOrderId())).thenReturn(order);
        when(expertServiceService.existsByExpertIdAndServiceId(expert.getId(), service.getId())).thenReturn(true);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> suggestionService.registerSuggestionForOrder(req, principal));
        assertEquals("Order is not waiting for any suggestions.", ex.getMessage());
    }

    @Test
    void updateWithDTO_success() {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(1);

        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setId(1);

        Expert expert = new Expert();
        expert.setId(10);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setId(100);

        Order order = new Order();
        order.setService(service);
        suggestion.setOrder(order);


        when(suggestionRepository.findById(req.getId())).thenReturn(Optional.of(suggestion));
        when(expertServiceService.existsByExpertIdAndServiceId(expert.getId(), service.getId())).thenReturn(true);
        when(expertService.findById(expert.getId())).thenReturn(expert);
        doNothing().when(suggestionMapper).updateEntityWithDTO(req, suggestion);
        when(suggestionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Suggestion updated = suggestionService.updateWithDTO(req,expert.getId());

        assertNotNull(updated);
        assertEquals(expert, updated.getExpert());
    }

    @Test
    void updateWithDTO_expertServiceNotExists_throws() {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(1);

        ir.maktabsharif.home_service.model.service.Service service = new ir.maktabsharif.home_service.model.service.Service();
        service.setId(100);
        Order order = new Order();
        order.setService(service);
        suggestion.setOrder(order);

        SuggestionSaveUpdateRequest req = new SuggestionSaveUpdateRequest();
        req.setId(1);

        Expert expert = new Expert();
        expert.setId(10);

        when(suggestionRepository.findById(req.getId())).thenReturn(Optional.of(suggestion));
        when(expertServiceService.existsByExpertIdAndServiceId(expert.getId(), service.getId())).thenReturn(false);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> suggestionService.updateWithDTO(req,expert.getId()));
        assertEquals("Expert Id and Service Id are not registered", ex.getMessage());
    }

    @Test
    void findAllAndSortByPriceAsc_success() {
        Order order = new Order();
        order.setId(1);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);

        Page<Suggestion> page = new PageImpl<>(Collections.singletonList(suggestion));
        Pageable pageable = Pageable.unpaged();

        when(orderService.findById(1)).thenReturn(order);
        when(suggestionRepository.findAllByOrderAndSortByPriceAsc(order, pageable)).thenReturn(page);

        Page<Suggestion> result = suggestionService.findAllAndSortByPriceAsc(1, pageable);

        assertFalse(result.isEmpty());
    }

    // --- findAllAndSortByPriceAsc وقتی خالی است خطا ---
    @Test
    void findAllAndSortByPriceAsc_empty_throws() {
        Order order = new Order();
        order.setId(1);

        Pageable pageable = Pageable.unpaged();

        when(orderService.findById(1)).thenReturn(order);
        when(suggestionRepository.findAllByOrderAndSortByPriceAsc(order, pageable)).thenReturn(Page.empty());

        assertThrows(NoElementFoundException.class, () -> suggestionService.findAllAndSortByPriceAsc(1, pageable));
    }

    @Test
    void findAllByAndSortByExpertScoreDesc_success() {
        Order order = new Order();
        order.setId(1);

        Suggestion suggestion = new Suggestion();
        suggestion.setOrder(order);

        Page<Suggestion> page = new PageImpl<>(Collections.singletonList(suggestion));
        Pageable pageable = Pageable.unpaged();

        when(orderService.findById(1)).thenReturn(order);
        when(suggestionRepository.findAllByOrderAndSortByExpertScoreDesc(order, pageable)).thenReturn(page);

        Page<Suggestion> result = suggestionService.findAllByAndSortByExpertScoreDesc(1, pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllByAndSortByExpertScoreDesc_empty_throws() {
        Order order = new Order();
        order.setId(1);

        Pageable pageable = Pageable.unpaged();

        when(orderService.findById(1)).thenReturn(order);
        when(suggestionRepository.findAllByOrderAndSortByExpertScoreDesc(order, pageable)).thenReturn(Page.empty());

        assertThrows(NoElementFoundException.class, () -> suggestionService.findAllByAndSortByExpertScoreDesc(1, pageable));
    }

    @Test
    void existsByOrderIdAndExpertIdAndAcceptedTrue_success() {
        when(suggestionRepository.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 2)).thenReturn(true);
        assertTrue(suggestionService.existsByOrderIdAndExpertIdAndAcceptedTrue(1, 2));
    }

    @Test
    void findAllByExpertId_success() {
        Suggestion suggestion = new Suggestion();
        Page<Suggestion> page = new PageImpl<>(Collections.singletonList(suggestion));
        Pageable pageable = Pageable.unpaged();

        when(suggestionRepository.findAllByExpertId(10, pageable)).thenReturn(page);
        when(suggestionMapper.mapToResponse(any())).thenReturn(mock(SuggestionFindResponse.class));

        Page<SuggestionFindResponse> result = suggestionService.findAllByExpertId(10, pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllByExpertId_empty_throws() {
        Pageable pageable = Pageable.unpaged();
        when(suggestionRepository.findAllByExpertId(10, pageable)).thenReturn(Page.empty());
        assertThrows(NoElementFoundException.class, () -> suggestionService.findAllByExpertId(10, pageable));
    }

    @Test
    void confirmSuggestionAcceptance_success() {
        Suggestion suggestion = new Suggestion();
        suggestion.setAccepted(false);

        when(suggestionRepository.findById(1)).thenReturn(Optional.of(suggestion));
        when(suggestionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        suggestionService.confirmSuggestionAcceptance(1);

        assertTrue(suggestion.getAccepted());
    }

    @Test
    void findByOrderId_success() {
        Suggestion suggestion = new Suggestion();
        when(suggestionRepository.findByOrderId(1)).thenReturn(Optional.of(suggestion));
        Suggestion result = suggestionService.findByOrderId(1);
        assertEquals(suggestion, result);
    }

    @Test
    void findByOrderId_notFound_throws() {
        when(suggestionRepository.findByOrderId(1)).thenReturn(Optional.empty());
        assertThrows(NoElementFoundException.class, () -> suggestionService.findByOrderId(1));
    }
}
