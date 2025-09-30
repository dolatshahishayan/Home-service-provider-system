package ir.maktabsharif.home_service.mapper.expert;

import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Expert;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExpertMapper {
    Expert mapToEntity(ExpertSaveUpdateRequest expertSaveUpdateRequest);
    void updateEntityWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest,@MappingTarget Expert expert);
    ExpertFindResponse mapToResponse(Expert expert);
}