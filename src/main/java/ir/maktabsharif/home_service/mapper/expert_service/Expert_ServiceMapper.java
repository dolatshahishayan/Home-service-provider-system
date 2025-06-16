package ir.maktabsharif.home_service.mapper.expert_service;

import ir.maktabsharif.home_service.dto.expert_service.Expert_ServiceFindResponse;
import ir.maktabsharif.home_service.dto.expert_service.Expert_ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface Expert_ServiceMapper {
    Expert_Service mapToEntity(Expert_ServiceSaveUpdateRequest expert_serviceSaveUpdateRequest);
    Expert_ServiceFindResponse mapToDTO(Expert_Service expert_service);
}
