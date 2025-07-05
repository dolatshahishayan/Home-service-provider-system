package ir.maktabsharif.home_service.mapper.suggestion;

import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SuggestionMapper {
    Suggestion mapToEntity(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);
    SuggestionFindResponse mapToResponse(Suggestion suggestion);
}
