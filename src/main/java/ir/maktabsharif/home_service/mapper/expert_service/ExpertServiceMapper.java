package ir.maktabsharif.home_service.mapper.expert_service;

import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceFindResponse;
import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpertServiceMapper {
    ExpertService mapToEntity(ExpertServiceSaveUpdateRequest expert_serviceSaveUpdateRequest);
    @Mapping(source = "expert.id", target = "expertId")
    @Mapping(source = "service.id", target = "serviceId")
    ExpertServiceFindResponse mapToResponse(ExpertService expertService);
}
