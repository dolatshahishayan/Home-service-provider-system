package ir.maktabsharif.home_service.controller.expert_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceFindResponse;
import ir.maktabsharif.home_service.mapper.expert_service.ExpertServiceMapper;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expert-services")
@RequiredArgsConstructor
@Tag(name = "Expert-services controller", description = "Controller class for expert-services")
public class ExpertServiceController {

    private final ExpertServiceService expertServiceService;
    private final ExpertServiceMapper expertServiceMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/add-expert-to-service")
    @Operation(summary = "Add expert to service", description = "Method for adding expert to a service")
    public ResponseEntity<String> addExpertToService(@RequestParam Integer expertId, @RequestParam Integer serviceId) {
        expertServiceService.addExpertToService(expertId, serviceId);
        return ResponseEntity.ok("expert added to service");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/remove-expert-from-service")
    @Operation(summary = "Remove expert from service", description = "Method for removing expert from a service")
    public ResponseEntity<String> removeExpertFromService(@RequestParam Integer expertId, @RequestParam Integer serviceId) {
        expertServiceService.removeExpertFromService(expertId, serviceId);
        return ResponseEntity.ok("expert removed from service");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/find-by-expert-id-and-service-id")
    @Operation(summary = "Find by expert id and service id", description = "Returns an expert_service object by expert id and service id")
    public ResponseEntity<ExpertServiceFindResponse> findByExpertIdAndServiceId(@RequestParam Integer expertId, @RequestParam Integer serviceId) {
        ExpertService byExpertIdAndServiceId = expertServiceService.findByExpertIdAndServiceId(expertId, serviceId);
        return ResponseEntity.ok(expertServiceMapper.mapToResponse(byExpertIdAndServiceId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/exists-by-service-id-and-expert-id")
    @Operation(summary = "Exists by service id and expert id", description = "Checks if an expert is in a service or not")
    public ResponseEntity<Boolean> existsByServiceIdAndExpertId(@RequestParam Integer serviceId, @RequestParam Integer expertId) {
        return ResponseEntity.ok(expertServiceService.existsByExpertIdAndServiceId(expertId, serviceId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/find-by-expert-id")
    @Operation(summary = "Find by expert id", description = "Finds a list of expert service objects by expert id")
    public ResponseEntity<Page<ExpertServiceFindResponse>> findByExpertId(@RequestParam Integer expertId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<ExpertService> byExpertId = expertServiceService.findByExpertId(expertId, PageRequest.of(page, size));
        return ResponseEntity.ok(byExpertId.map(expertServiceMapper::mapToResponse));
    }
}
