package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SuggestionServiceImpl extends BaseServiceImpl<Suggestion, Integer, SuggestionRepository, SuggestionMapper> implements SuggestionService {
    protected final ExpertService expertService;
    protected final OrderService orderService;

    public SuggestionServiceImpl(SuggestionRepository repository, SuggestionMapper suggestionMapper, @Lazy ExpertService expertService,@Lazy OrderService orderService) {
        super(repository, suggestionMapper);
        this.expertService = expertService;
        this.orderService = orderService;
    }

    @Override
    public Suggestion registerSuggestionForOrder(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest) {
        Suggestion suggestion = mapper.mapToEntity(suggestionSaveUpdateRequest);
        suggestion.setCreationDate(LocalDateTime.now());
        suggestion.setExpert(expertService.findById(suggestionSaveUpdateRequest.getExpertId()));
        suggestion.setOrder(orderService.findById(suggestionSaveUpdateRequest.getOrderId()));
        if (!suggestion.getOrder().getOrderStatus().equals(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION)) {
            throw new InvalidRequestException("Order is not waiting for any suggestions.");
        }
        if (suggestion.getPrice() < suggestion.getOrder().getService().getBasePrice()) {
            throw new InvalidRequestException("Price must be greater than the base price.");
        }
        return save(suggestion);
    }

    @Override
    public Suggestion updateWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest) {
        Suggestion suggestion = findById(suggestionSaveUpdateRequest.getId());
        mapper.updateEntityWithDTO(suggestionSaveUpdateRequest, suggestion);
        suggestion.setExpert(expertService.findById(suggestionSaveUpdateRequest.getExpertId()));
        suggestion.setOrder(orderService.findById(suggestionSaveUpdateRequest.getOrderId()));
        return save(suggestion);
    }

    @Override
    public List<Suggestion> findAllAndSortByPriceAsc(Integer orderId) {
        Order byId = orderService.findById(orderId);
        List<Suggestion> allByOrderAndSortByPriceAsc = repository.findAllByOrderAndSortByPriceAsc(byId);
        if (allByOrderAndSortByPriceAsc.isEmpty()) {
            throw new NoElementFoundException();
        }
        return allByOrderAndSortByPriceAsc;
    }

    @Override
    public List<Suggestion> findAllByAndSortByExpertScoreDesc(Integer orderId) {
        Order byId = orderService.findById(orderId);
        List<Suggestion> allByOrderAndSortByExpertScoreDesc = repository.findAllByOrderAndSortByExpertScoreDesc(byId);
        if (allByOrderAndSortByExpertScoreDesc.isEmpty()) {
            throw new NoElementFoundException();
        }
        return allByOrderAndSortByExpertScoreDesc;
    }

    @Override
    public List<SuggestionFindResponse> findAllByExpertId(Integer expertId) {
        List<Suggestion> allByExpertId = repository.findAllByExpertId(expertId);
        if (allByExpertId.isEmpty()) {
            throw new NoElementFoundException();
        }
        List<SuggestionFindResponse> responses = new ArrayList<>();
        for (Suggestion suggestion : allByExpertId) {
            responses.add(mapper.mapToResponse(suggestion));
        }
        return responses;
    }

    @Override
    public void confirmSuggestionAcceptance(Integer suggestionId) {
        Suggestion suggestion = findById(suggestionId);
        suggestion.setAccepted(true);
        save(suggestion);
    }
}
