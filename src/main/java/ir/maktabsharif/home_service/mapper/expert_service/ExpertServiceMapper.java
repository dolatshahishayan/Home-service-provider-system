package ir.maktabsharif.home_service.mapper.expert_service;

import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceFindResponse;
import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpertServiceMapper {

    @Mapping(target = "id", expression = "java(new ir.maktabsharif.home_service.model.expert_service.ExpertServiceId(expert_serviceSaveUpdateRequest.getExpertId(), expert_serviceSaveUpdateRequest.getServiceId()))")
    @Mapping(target = "expert", ignore = true)
    @Mapping(target = "service", ignore = true)
    ExpertService mapToEntity(ExpertServiceSaveUpdateRequest expert_serviceSaveUpdateRequest);

    @Mapping(target = "expertId", source = "id.expertId")
    @Mapping(target = "serviceId", source = "id.serviceId")
    ExpertServiceFindResponse mapToResponse(ExpertService expertService);
}
