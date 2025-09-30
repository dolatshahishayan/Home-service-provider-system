package ir.maktabsharif.home_service.mapper.admin;

import ir.maktabsharif.home_service.dto.admin.AdminFindResponse;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdminMapper {
    Admin mapToEntity(AdminSaveUpdateRequest adminSaveUpdateRequest);
    void updateEntityWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest,@MappingTarget Admin admin);
    AdminFindResponse mapToResponse(Admin admin);
}