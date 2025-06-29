package ir.maktabsharif.home_service.service.admin;

import com.fasterxml.jackson.databind.ser.Serializers;
import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Admin;

public interface AdminService extends BaseService<Admin, AdminSaveUpdateRequest> {
    void saveWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest);
    void updateWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest);
}
