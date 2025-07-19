package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuggestionService extends BaseService<Suggestion, Integer> {
    void confirmSuggestionAcceptance(Integer suggestionId);

    Page<SuggestionFindResponse> findAllByExpertId(Integer expertId, Pageable pageable);

    Suggestion registerSuggestionForOrder(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);

    Suggestion updateWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);

    Page<Suggestion> findAllAndSortByPriceAsc(Integer orderId,Pageable pageable);

    Page<Suggestion> findAllByAndSortByExpertScoreDesc(Integer orderId,Pageable pageable);

    boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId, Integer expertId);
}
