package ir.maktabsharif.home_service.controller.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Services controller", description = "Controller class for services")
public class ServiceController {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/save")
    @Operation(summary = "Save service", description = "Method for saving a service")
    public ResponseEntity<ServiceFindResponse> save(@RequestBody @Validated(ValidationGroup.Save.class) ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        Service saved = serviceService.saveWithDTO(serviceSaveUpdateRequest);
        return ResponseEntity.ok(serviceMapper.mapToResponse(saved));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update")
    @Operation(summary = "Update service", description = "Method for update a service")
    public ResponseEntity<ServiceFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) ServiceSaveUpdateRequest serviceSaveUpdateRequest) {
        Service updated = serviceService.updateWithDTO(serviceSaveUpdateRequest);
        return ResponseEntity.ok(serviceMapper.mapToResponse(updated));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update-description")
    @Operation(summary = "Update description", description = "Update a service's description")
    public ResponseEntity<String> updateDescription(@RequestParam Integer serviceId, @RequestParam String description) {
        serviceService.updateDescription(serviceId, description);
        return ResponseEntity.ok("Description updated");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update-base-price")
    @Operation(summary = "Update base price", description = "Update a service's base price")
    public ResponseEntity<String> updateBasePrice(@RequestParam Integer serviceId, @RequestParam Double basePrice) {
        serviceService.updateBasePrice(serviceId, basePrice);
        return ResponseEntity.ok("Base price updated");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/exists-by-name")
    @Operation(summary = "Exists by name", description = "Checks if a service exists by name")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        return ResponseEntity.ok(serviceService.existsByName(name));
    }

    @GetMapping("/find-all-parentServices")
    @Operation(summary = "Find all ParentServices", description = "Finds all parent services")
    public ResponseEntity<Page<ServiceFindResponse>> findAllParentServices(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<Service> allAndParentServiceIsNull = serviceService.findAllAndParentServiceIsNull(PageRequest.of(page, size));
        return ResponseEntity.ok(allAndParentServiceIsNull.map(serviceMapper::mapToResponse));
    }

    @GetMapping("/find-all-subServices")
    @Operation(summary = "Find all subServices", description = "Finds all subServices")
    public ResponseEntity<Page<ServiceFindResponse>> findAllSubServices(@RequestParam Integer serviceId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Service byId = serviceService.findById(serviceId);
        Page<Service> allAndParentServiceIsNotNullByParentService = serviceService.findAllAndParentServiceIsNotNullByParentService(byId, PageRequest.of(page, size));
        return ResponseEntity.ok(allAndParentServiceIsNotNullByParentService.map(serviceMapper::mapToResponse));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    @Operation(summary = "Delete service", description = "Method for deleting a service")
    public ResponseEntity<String> delete(@RequestParam Integer serviceId) {
        serviceService.deleteById(serviceId);
        return ResponseEntity.ok("Deleted service");
    }
}
