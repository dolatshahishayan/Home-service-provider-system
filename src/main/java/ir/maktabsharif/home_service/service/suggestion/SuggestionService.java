package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;

import java.util.List;

public interface SuggestionService extends BaseService<Suggestion,Integer> {
    void confirmSuggestionAcceptance(Integer suggestionId);
    List<SuggestionFindResponse> findAllByExpertId(Integer expertId);
    Suggestion registerSuggestionForOrder(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);
    Suggestion updateWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);
    List<Suggestion> findAllAndSortByPriceAsc(Integer orderId);
    List<Suggestion> findAllByAndSortByExpertScoreDesc(Integer orderId);
}
