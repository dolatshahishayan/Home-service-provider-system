package ir.maktabsharif.home_service.mapper.suggestion;

import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SuggestionMapper {
    Suggestion mapToEntity(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest);
    void updateEntityWithDTO(SuggestionSaveUpdateRequest suggestionSaveUpdateRequest, @MappingTarget Suggestion suggestion);
    @Mapping(source = "expert.id", target = "expertId")
    @Mapping(source = "order.id", target = "orderId")
    SuggestionFindResponse mapToResponse(Suggestion suggestion);
}