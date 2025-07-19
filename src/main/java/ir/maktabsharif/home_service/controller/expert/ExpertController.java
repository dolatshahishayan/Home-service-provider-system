package ir.maktabsharif.home_service.controller.expert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/experts")
@RequiredArgsConstructor
@Tag(name = "Experts controller", description = "Controller class for experts")
public class ExpertController {

    private final ExpertService expertService;
    private final ExpertMapper expertMapper;
    private final JwtUtil jwtUtil;

    @PostMapping("/save")
    @Operation(summary = "Save expert", description = "Save method for expert")
    public ResponseEntity<ExpertFindResponse> saveExpert(@RequestBody @Validated(ValidationGroup.Save.class) ExpertSaveUpdateRequest expert, @RequestParam(required = false) String imagePath, HttpServletResponse response) {
        Expert register = expertService.register(expert, imagePath);
        String token = jwtUtil.generateToken(new UserDetailsImpl(register));
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(expertMapper.mapToResponse(register));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/verify")
    @Operation(summary = "Verify expert", description = "Verify method for expert")
    public ResponseEntity<String> verifyExpert(@RequestParam Integer expertId) {
        expertService.updateStatusToVerified(expertId);
        return ResponseEntity.ok("expert verified");
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PutMapping("/update")
    @Operation(summary = "Update expert", description = "Update method for expert")
    public ResponseEntity<ExpertFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) ExpertSaveUpdateRequest expertSaveUpdateRequest, @RequestParam(required = false) String imagePath, HttpServletResponse response) {
        Expert expert = expertService.updateWithDTO(expertSaveUpdateRequest, imagePath);
        String token = jwtUtil.generateToken(new UserDetailsImpl(expert));
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(expertMapper.mapToResponse(expert));
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find by email", description = "Method for finding expert by email")
    public ResponseEntity<ExpertFindResponse> findExpertByEmail(@RequestParam String email) {
        Expert expert = expertService.findByEmail(email);
        return ResponseEntity.ok(expertMapper.mapToResponse(expert));
    }
}
