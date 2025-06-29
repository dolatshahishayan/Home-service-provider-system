package ir.maktabsharif.home_service.mapper.expert;

import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Expert;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpertMapper {
    Expert mapToEntity(ExpertSaveUpdateRequest expertSaveUpdateRequest);
}
