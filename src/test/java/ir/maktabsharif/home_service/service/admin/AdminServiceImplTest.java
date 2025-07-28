package ir.maktabsharif.home_service.service.admin;

import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.admin.AdminMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.repository.admin.AdminRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void saveWithDTO_shouldSaveAdminSuccessfully() {
        AdminSaveUpdateRequest request = new AdminSaveUpdateRequest();
        request.setEmail("admin@example.com");
        request.setPassword("1234");

        Admin admin = new Admin();
        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(adminMapper.mapToEntity(request)).thenReturn(admin);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded123");
        when(adminRepository.save(admin)).thenReturn(admin);

        Admin result = adminService.saveWithDTO(request);

        assertEquals("encoded123", result.getPassword());
        assertEquals(Role.ROLE_ADMIN, result.getRole());
        assertTrue(result.getIsEmailVerified());
        assertEquals("admin@example.com", result.getEmail());
        assertNotNull(result.getRegistrationDate());
        verify(adminRepository).save(admin);
    }

    @Test
    void saveWithDTO_shouldThrowIfEmailExists() {
        AdminSaveUpdateRequest request = new AdminSaveUpdateRequest();
        request.setEmail("admin@example.com");

        when(userService.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> adminService.saveWithDTO(request));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void updateWithDTO_shouldUpdateAdminSuccessfully() {
        AdminSaveUpdateRequest request = new AdminSaveUpdateRequest();
        request.setId(1);
        request.setEmail("admin@example.com");
        request.setPassword("newPass");

        Admin existingAdmin = new Admin();
        existingAdmin.setId(1);
        existingAdmin.setEmail("old@example.com");

        when(userService.existsByEmailAndIdNot("admin@example.com", 1)).thenReturn(false);
        when(adminRepository.findById(1)).thenReturn(Optional.of(existingAdmin));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        doAnswer(invocation -> {
            AdminSaveUpdateRequest dto = invocation.getArgument(0);
            Admin admin = invocation.getArgument(1);
            admin.setEmail(dto.getEmail());
            return null;
        }).when(adminMapper).updateEntityWithDTO(eq(request), any(Admin.class));
        when(adminRepository.save(existingAdmin)).thenReturn(existingAdmin);

        Admin result = adminService.updateWithDTO(request);

        assertEquals("encodedPass", result.getPassword());
        assertEquals("admin@example.com", result.getEmail());
    }

    @Test
    void updateWithDTO_shouldThrowIfEmailAlreadyExists() {
        AdminSaveUpdateRequest request = new AdminSaveUpdateRequest();
        request.setEmail("admin@example.com");
        request.setId(1);

        when(userService.existsByEmailAndIdNot(request.getEmail(), request.getId())).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> adminService.updateWithDTO(request));
        verify(adminRepository, never()).save(any());
    }

    @Test
    void findByEmail_shouldReturnAdmin() {
        Admin admin = new Admin();
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        Admin result = adminService.findByEmail("admin@example.com");

        assertEquals(admin, result);
    }

    @Test
    void findByEmail_shouldThrowIfNotFound() {
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThrows(NoElementFoundException.class, () -> adminService.findByEmail("admin@example.com"));
    }

}
