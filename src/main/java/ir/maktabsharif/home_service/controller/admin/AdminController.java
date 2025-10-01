package ir.maktabsharif.home_service.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.admin.AdminFindResponse;
import ir.maktabsharif.home_service.dto.admin.AdminSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.admin.AdminMapper;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.service.admin.AdminService;
import ir.maktabsharif.home_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admins controller", description = "Controller class for admins")
public class AdminController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final JwtUtil jwtUtil;


    @PostMapping("/save")
    @Operation(summary = "Save admin", description = "Save method for admin")
    public ResponseEntity<AdminFindResponse> saveAdmin(@RequestBody @Validated(ValidationGroup.Save.class) AdminSaveUpdateRequest adminSaveUpdateRequest, HttpServletResponse response) {
        Admin admin = adminService.saveWithDTO(adminSaveUpdateRequest);
        String token = jwtUtil.generateToken(new UserDetailsImpl(admin));
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(adminMapper.mapToResponse(admin));
    }

    @PutMapping("/update")
    @Operation(summary = "Update admin", description = "Update method for admin")
    public ResponseEntity<AdminFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) AdminSaveUpdateRequest adminSaveUpdateRequest, HttpServletResponse response) {
        Admin admin = adminService.updateWithDTO(adminSaveUpdateRequest);
        String token = jwtUtil.generateToken(new UserDetailsImpl(admin));
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(adminMapper.mapToResponse(admin));
    }
}
