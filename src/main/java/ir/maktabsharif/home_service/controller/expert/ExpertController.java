package ir.maktabsharif.home_service.controller.expert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expert")
@RequiredArgsConstructor
@Tag(name = "Expert controller", description = "Controller class for expert")
public class ExpertController {

    private final ExpertService expertService;
    private final ExpertMapper expertMapper;

    @PostMapping("/save")
    @Operation(summary = "Save expert", description = "Save method for expert")
    public ResponseEntity<ExpertFindResponse> saveExpert(@RequestBody @Validated(ValidationGroup.Save.class) ExpertSaveUpdateRequest expert, @RequestParam(required = false) String imagePath, HttpSession session) {
        Expert register = expertService.register(expert, imagePath);
        session.setAttribute("currentUser", new UserSessionDTO(register.getId(), register.getEmail(), Role.EXPERT));
        return ResponseEntity.ok(expertMapper.mapToResponse(register));
    }

    @PutMapping("/verify")
    @Operation(summary = "Verify expert", description = "Verify method for expert")
    public ResponseEntity<String> verifyExpert(@RequestParam Integer expertId) {
        expertService.updateStatusToVerified(expertId);
        return ResponseEntity.ok("expert verified");
    }

    @PutMapping("/update")
    @Operation(summary = "Update expert", description = "Update method for expert")
    public ResponseEntity<ExpertFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) ExpertSaveUpdateRequest expertSaveUpdateRequest, @RequestParam(required = false) String imagePath, HttpSession session) {
        Expert expert = expertService.updateWithDTO(expertSaveUpdateRequest,imagePath);
        session.setAttribute("currentUser", new UserSessionDTO(expert.getId(), expert.getEmail(), Role.EXPERT));
        return ResponseEntity.ok(expertMapper.mapToResponse(expert));
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find by email", description = "Method for finding expert by email")
    public ResponseEntity<ExpertFindResponse> findExpertByEmail(@RequestParam String email) {
        Expert expert = expertService.findByEmail(email);
        return ResponseEntity.ok(expertMapper.mapToResponse(expert));
    }
}
