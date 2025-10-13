package ir.maktabsharif.home_service.controller.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers controller", description = "Controller class for customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Save customer", description = "Save method for customer")
    public ResponseEntity<CustomerFindResponse> saveCustomer(@RequestBody @Validated(ValidationGroup.Save.class) CustomerSaveUpdateRequest customer, HttpServletResponse response) {
        Customer register = customerService.register(customer);
        String token = jwtUtil.generateToken(new UserDetailsImpl(register));
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(customerMapper.mapToResponse(register));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping
    @Operation(summary = "Update customer", description = "Update method for customer")
    public ResponseEntity<CustomerFindResponse> updateCustomer(@RequestBody @Validated(ValidationGroup.Update.class) CustomerSaveUpdateRequest customer, HttpServletResponse response) {
        Customer updated = customerService.updateWithDTO(customer);
        String token = jwtUtil.generateToken(new UserDetailsImpl(updated));
        response.addHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(customerMapper.mapToResponse(updated));
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find by email", description = "Find a customer with email")
    public ResponseEntity<CustomerFindResponse> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(customerMapper.mapToResponse(customerService.findByEmail(email)));
    }
}
