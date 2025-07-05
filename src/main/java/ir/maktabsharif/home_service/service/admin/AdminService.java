package ir.maktabsharif.home_service.service.admin;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.model.user.Customer;

public interface AdminService extends BaseService<Admin,Integer> {
    Admin saveWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest);
    Admin updateWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest);
    Admin findByEmail(String email);
}
