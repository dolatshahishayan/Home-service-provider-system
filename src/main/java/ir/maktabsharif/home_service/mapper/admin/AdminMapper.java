package ir.maktabsharif.home_service.mapper.admin;

import ir.maktabsharif.home_service.dto.admin.AdminFindResponse;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminMapper {
    Admin mapToEntity(AdminSaveUpdateRequest adminSaveUpdateRequest);
}
