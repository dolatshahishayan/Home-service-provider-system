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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AdminServiceImpl extends BaseServiceImpl<Admin, Integer, AdminRepository, AdminMapper> implements AdminService {
    protected final UserService userService;
    protected final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminRepository repository, AdminMapper adminMapper, UserService userService, PasswordEncoder passwordEncoder) {
        super(repository, adminMapper);
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Admin saveWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest) {
        if (userService.existsByEmail(adminSaveUpdateRequest.getEmail())) {
            throw new UserWithSameEmailExistsException();
        }
        Admin admin = mapper.mapToEntity(adminSaveUpdateRequest);
        admin.setPassword(passwordEncoder.encode(adminSaveUpdateRequest.getPassword()));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEmailVerified(true);
        admin.setEmail(adminSaveUpdateRequest.getEmail().toLowerCase());
        admin.setRegistrationDate(LocalDateTime.now());
        return save(admin);
    }

    @Override
    public Admin updateWithDTO(AdminSaveUpdateRequest adminSaveUpdateRequest) {
        if (userService.existsByEmailAndIdNot(adminSaveUpdateRequest.getEmail(), adminSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        Admin byId = findById(adminSaveUpdateRequest.getId());
        if (adminSaveUpdateRequest.getPassword() != null) {
            byId.setPassword(passwordEncoder.encode(adminSaveUpdateRequest.getPassword()));
        }
        mapper.updateEntityWithDTO(adminSaveUpdateRequest, byId);
        byId.setEmail(adminSaveUpdateRequest.getEmail().toLowerCase());
        return save(byId);
    }

    @Override
    public Admin findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(NoElementFoundException::new);
    }

}
