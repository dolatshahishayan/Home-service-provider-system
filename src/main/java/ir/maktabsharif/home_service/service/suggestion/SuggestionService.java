package ir.maktabsharif.home_service.service.suggestion;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;

import java.util.List;

public interface SuggestionService extends BaseService<Suggestion, SuggestionSaveUpdateRequest> {
    void confirmSuggestionAcceptance(Integer suggestionId);
    List<SuggestionFindResponse> findAllByExpertId(Integer expertId);
    void saveWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);
    void updateWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);
}
