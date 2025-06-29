package ir.maktabsharif.home_service.service.admin;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.admin.AdminMapper;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.repository.admin.AdminRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl extends BaseServiceImpl<Admin, AdminSaveUpdateRequest, AdminRepository, AdminMapper> implements AdminService {
    protected final UserService userService;

    public AdminServiceImpl(AdminRepository repository, AdminMapper mapper, UserService userService) {
        super(repository, mapper);
        this.userService = userService;
    }

    @Override
    public void saveWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest) {
        if (userService.existsByEmail(adminSaveUpdateRequest.getEmail())) {
            throw new UserWithSameEmailExistsException();
        }
        Admin admin = mapper.mapToEntity(adminSaveUpdateRequest);
        admin.setRegistrationDate(LocalDateTime.now());
        save(admin);
    }
    @Override
    public void updateWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest) {
        if (userService.existsByEmailAndIdNot(adminSaveUpdateRequest.getEmail(), adminSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        update(mapper.mapToEntity(adminSaveUpdateRequest));
    }

}
