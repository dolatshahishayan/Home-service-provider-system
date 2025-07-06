package ir.maktabsharif.home_service.controller.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/service")
@RequiredArgsConstructor
@Tag(name = "Service controller", description = "Controller class for service")
public class ServiceController {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper;

    @PostMapping("/save")
    @Operation(summary = "Save service", description = "Method for saving a service")
    public ResponseEntity<ServiceFindResponse> save(@RequestBody ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        Service saved = serviceService.saveWithDTO(serviceSaveUpdateRequest);
        return ResponseEntity.ok(serviceMapper.mapToResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Update service", description = "Method for update a service")
    public ResponseEntity<ServiceFindResponse> update(@RequestBody ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        Service updated = serviceService.updateWithDTO(serviceSaveUpdateRequest);
        return ResponseEntity.ok(serviceMapper.mapToResponse(updated));
    }

    @PutMapping("/update-description")
    @Operation(summary = "Update description",description = "Update a service's description")
    public ResponseEntity<String> updateDescription(@RequestParam Integer serviceId, @RequestParam String description) {
        serviceService.updateDescription(serviceId, description);
        return ResponseEntity.ok("Description updated");
    }

    @PutMapping("/update-base-price")
    @Operation(summary = "Update base price",description = "Update a service's base price")
    public ResponseEntity<String> updateBasePrice(@RequestParam Integer serviceId, @RequestParam Double basePrice) {
        serviceService.updateBasePrice(serviceId,basePrice);
        return ResponseEntity.ok("Base price updated");
    }

    @GetMapping("/exists-by-name")
    @Operation(summary = "Exists by name",description = "Checks if a service exists by name")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        return ResponseEntity.ok(serviceService.existsByName(name));
    }

    @GetMapping("/find-all-ParentServices")
    @Operation(summary = "Find all ParentServices",description = "Finds all subServices")
    public ResponseEntity<List<ServiceFindResponse>> findAllParentServices() {
        List<Service> allAndParentServiceIsNull = serviceService.findAllAndParentServiceIsNull();
        List<ServiceFindResponse> responses=new ArrayList<>();
        for (Service service : allAndParentServiceIsNull) {
            responses.add(serviceMapper.mapToResponse(service));
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/find-all-subServices")
    @Operation(summary = "Find all subServices",description = "Finds all subServices")
    public ResponseEntity<List<ServiceFindResponse>> findAllSubServices(@RequestParam Integer serviceId) {
        Service byId = serviceService.findById(serviceId);
        List<Service> allAndParentServiceIsNotNullByParentService = serviceService.findAllAndParentServiceIsNotNullByParentService(byId);
        List<ServiceFindResponse> responses=new ArrayList<>();
        for (Service service : allAndParentServiceIsNotNullByParentService) {
            responses.add(serviceMapper.mapToResponse(service));
        }
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete service",description = "Method for deleting a service")
    public ResponseEntity<String> delete(@RequestParam Integer serviceId) {
        serviceService.delete(serviceId);
        return ResponseEntity.ok("Deleted service");
    }
}
