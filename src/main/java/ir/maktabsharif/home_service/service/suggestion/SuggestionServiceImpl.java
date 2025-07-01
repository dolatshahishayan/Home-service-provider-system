package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.repository.suggestion.SuggestionRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionServiceImpl extends BaseServiceImpl<Suggestion, SuggestionRepository, SuggestionMapper> implements SuggestionService {
    protected final ExpertService expertService;
    protected final OrderService orderService;

    public SuggestionServiceImpl(SuggestionRepository repository, SuggestionMapper mapper, @Lazy ExpertService expertService,@Lazy OrderService orderService) {
        super(repository, mapper);
        this.expertService = expertService;
        this.orderService = orderService;
    }

    @Override
    public void saveWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest) {
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
        save(suggestion);
    }

    @Override
    public void updateWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest) {
        update(mapper.mapToEntity(suggestionSaveUpdateRequest));
    }

    @Override
    public List<SuggestionFindResponse> findAllByExpertId(Integer expertId) {
        List<Suggestion> allByExpertId = repository.findAllByExpertId(expertId);
        if (allByExpertId.isEmpty()) {
            throw new NoElementFoundException();
        }
        List<SuggestionFindResponse> responses = new ArrayList<>();
        for (Suggestion suggestion : allByExpertId) {
            responses.add(mapper.mapToDTO(suggestion));
        }
        return responses;
    }

    @Override
    public void confirmSuggestionAcceptance(Integer suggestionId) {
        Suggestion suggestion = findById(suggestionId);
        suggestion.setAccepted(true);
        update(suggestion);
    }
}
