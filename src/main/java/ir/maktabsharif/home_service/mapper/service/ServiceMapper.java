package ir.maktabsharif.home_service.mapper.service;

import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.service.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceMapper {
    Service mapToEntity(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    @Mapping(source = "parentService.id", target = "parentServiceId")
    ServiceFindResponse mapToResponse(Service service);
}
