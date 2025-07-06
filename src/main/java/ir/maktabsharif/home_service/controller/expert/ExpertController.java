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
@RequestMapping("/expert")
@RequiredArgsConstructor
@Tag(name = "Expert controller", description = "controller class for expert")
public class ExpertController {
    private final ExpertService expertService;
    private final ExpertMapper expertMapper;

    @PostMapping("/save")
    @Operation(summary = "save expert", description = "save method for expert")
    public ResponseEntity<ExpertFindResponse> saveExpert(@RequestBody @Validated(ValidationGroup.save.class) ExpertSaveUpdateRequest expert, @RequestParam String imagePath, HttpSession session) {
        Expert register = expertService.register(expert, imagePath);
        session.setAttribute("currentUser", new UserSessionDTO(register.getId(), register.getEmail(), Role.EXPERT));
        return ResponseEntity.ok(expertMapper.mapToResponse(register));
    }
}
