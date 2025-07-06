package ir.maktabsharif.home_service.service.admin;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.admin.AdminMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.repository.admin.AdminRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class AdminServiceImpl extends BaseServiceImpl<Admin,Integer, AdminRepository, AdminMapper> implements AdminService {
    protected final UserService userService;

    public AdminServiceImpl(AdminRepository repository, AdminMapper adminMapper, UserService userService) {
        super(repository, adminMapper);
        this.userService = userService;
    }

    @Override
    public Admin saveWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest) {
        if (userService.existsByEmail(adminSaveUpdateRequest.getEmail())) {
            throw new UserWithSameEmailExistsException();
        }
        Admin admin = mapper.mapToEntity(adminSaveUpdateRequest);
        admin.setRole(Role.ADMIN);
        admin.setEmail(adminSaveUpdateRequest.getEmail().toLowerCase());
        admin.setRegistrationDate(LocalDateTime.now());
        return save(admin);
    }
    @Override
    public Admin updateWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest) {
        if (userService.existsByEmailAndIdNot(adminSaveUpdateRequest.getEmail(), adminSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        Admin admin = save(mapper.mapToEntity(adminSaveUpdateRequest));
        admin.setEmail(adminSaveUpdateRequest.getEmail().toLowerCase());
        return save(admin);
    }

    @Override
    public Admin findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(NoElementFoundException::new);
    }

}
