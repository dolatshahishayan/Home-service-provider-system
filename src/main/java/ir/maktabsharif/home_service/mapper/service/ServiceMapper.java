package ir.maktabsharif.home_service.mapper.service;

import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.service.Service;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceMapper {
    Service mapToEntity(ServiceSaveUpdateRequest serviceSaveUpdateRequest);
    void updateEntityWithDTO(ServiceSaveUpdateRequest serviceSaveUpdateRequest, @MappingTarget Service service);
    @Mapping(source = "parentService.id", target = "parentServiceId")
    ServiceFindResponse mapToResponse(Service service);
}
