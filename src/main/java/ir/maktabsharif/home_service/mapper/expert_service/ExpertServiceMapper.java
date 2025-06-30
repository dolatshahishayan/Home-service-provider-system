package ir.maktabsharif.home_service.mapper.expert_service;

import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpertServiceMapper {
    ExpertService mapToEntity(ExpertServiceSaveUpdateRequest expert_serviceSaveUpdateRequest);
}
