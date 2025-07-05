package ir.maktabsharif.home_service.service.admin;

import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.admin.AdminMapper;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.admin.AdminRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminMapper mapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void saveWithDTO_ShouldThrowException_WhenEmailExists() {
        AdminSaveUpdateRequest dto = new AdminSaveUpdateRequest();
        dto.setEmail("admin@example.com");

        when(userService.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> adminService.saveWithDTO(dto));
    }

    @Test
    void saveWithDTO_ShouldSaveAdmin_WhenEmailIsUnique() {
        AdminSaveUpdateRequest dto = new AdminSaveUpdateRequest();
        dto.setEmail("newadmin@example.com");

        Admin mockAdmin = new Admin();

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(mockAdmin);

        adminService.saveWithDTO(dto);

        verify(adminRepository).save(mockAdmin);
        assertNotNull(mockAdmin.getRegistrationDate());
    }
    @Test
    void updateWithDTO_ShouldThrowException_WhenEmailUsedByAnotherUser() {
        AdminSaveUpdateRequest dto = new AdminSaveUpdateRequest();
        dto.setEmail("admin@example.com");
        dto.setId(1);

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> adminService.updateWithDTO(dto));
    }

    @Test
    void updateWithDTO_ShouldUpdateAdmin_WhenEmailValid() {
        AdminSaveUpdateRequest dto = new AdminSaveUpdateRequest();
        dto.setEmail("admin@example.com");
        dto.setId(1);

        Admin admin = new Admin();

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(admin);

        adminService.updateWithDTO(dto);

        verify(adminRepository).save(admin);
    }
    @Test
    void findByEmail_ShouldFindAdmin_WhenEmailExists() {
        Admin admin = new Admin();
        when(adminRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(admin));
        adminService.findByEmail("exists@example.com");
        verify(adminRepository).findByEmail("exists@example.com");
    }
}