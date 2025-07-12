package ir.maktabsharif.home_service.controller.expert_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceFindResponse;
import ir.maktabsharif.home_service.mapper.expert_service.ExpertServiceMapper;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expert-services")
@RequiredArgsConstructor
@Tag(name = "Expert-services controller", description = "Controller class for expert-services")
public class ExpertServiceController {

    private final ExpertServiceService expertServiceService;
    private final ExpertServiceMapper expertServiceMapper;

    @PostMapping("/add-expert-to-service")
    @Operation(summary = "Add expert to service", description = "Method for adding expert to a service")
    public ResponseEntity<String> addExpertToService(@RequestParam Integer expertId, @RequestParam Integer serviceId) {
        expertServiceService.addExpertToService(expertId, serviceId);
        return ResponseEntity.ok("expert added to service");
    }

    @DeleteMapping("/remove-expert-from-service")
    @Operation(summary = "Remove expert from service", description = "Method for removing expert from a service")
    public ResponseEntity<String> removeExpertFromService(@RequestParam Integer expertId, @RequestParam Integer serviceId) {
        expertServiceService.removeExpertFromService(expertId, serviceId);
        return ResponseEntity.ok("expert removed from service");
    }

    @GetMapping("/find-by-expert-id-and-service-id")
    @Operation(summary = "Find by expert id and service id", description = "Returns an expert_service object by expert id and service id")
    public ResponseEntity<ExpertServiceFindResponse> findByExpertIdAndServiceId(@RequestParam Integer expertId, @RequestParam Integer serviceId) {
        ExpertService byExpertIdAndServiceId = expertServiceService.findByExpertIdAndServiceId(expertId, serviceId);
        return ResponseEntity.ok(expertServiceMapper.mapToResponse(byExpertIdAndServiceId));
    }

    @GetMapping("/exists-by-service-id-and-expert-id")
    @Operation(summary = "Exists by service id and expert id", description = "Checks if an expert is in a service or not")
    public ResponseEntity<Boolean> existsByServiceIdAndExpertId(@RequestParam Integer serviceId, @RequestParam Integer expertId) {
        return ResponseEntity.ok(expertServiceService.existsByExpertIdAndServiceId(expertId, serviceId));
    }

    @GetMapping("/find-by-expert-id")
    @Operation(summary = "Find by expert id", description = "Finds a list of expert service objects by expert id")
    public ResponseEntity<List<ExpertServiceFindResponse>> findByExpertId(@RequestParam Integer expertId) {
        List<ExpertService> byExpertId = expertServiceService.findByExpertId(expertId);
        List<ExpertServiceFindResponse> responses = new ArrayList<>();
        for (ExpertService expertService : byExpertId) {
            responses.add(expertServiceMapper.mapToResponse(expertService));
        }
        return ResponseEntity.ok(responses);
    }
}
