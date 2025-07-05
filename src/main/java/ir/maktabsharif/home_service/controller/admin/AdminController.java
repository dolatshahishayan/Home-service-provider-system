package ir.maktabsharif.home_service.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.admin.AdminFindResponse;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.mapper.admin.AdminMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.admin.AdminService;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin controller",description = "Controller class for admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final AdminMapper adminMapper;

    @PostMapping("/save-admin")
    @Operation(summary = "save admin",description = "save method for admin")
    public ResponseEntity<AdminFindResponse> saveAdmin(@RequestBody AdminSaveUpdateRequest adminSaveUpdateRequest,HttpSession session) {
        Admin admin = adminService.saveWithDTO(adminSaveUpdateRequest);
        session.setAttribute("currentUser", new UserSessionDTO(admin.getId(), admin.getEmail(), Role.ADMIN));
        return ResponseEntity.ok(adminMapper.mapToResponse(admin));
    }

    @PutMapping("/update")
    @Operation(summary = "update admin",description = "update method for admin")
    public ResponseEntity<AdminFindResponse> update(@RequestBody AdminSaveUpdateRequest adminSaveUpdateRequest, HttpSession session){
        Admin admin = adminService.updateWithDTO(adminSaveUpdateRequest);
        session.setAttribute("currentUser", new UserSessionDTO(admin.getId(), admin.getEmail(),Role.ADMIN));
        return ResponseEntity.ok(adminMapper.mapToResponse(admin));
    }
}
