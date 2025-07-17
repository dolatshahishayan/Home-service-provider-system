package ir.maktabsharif.home_service.controller.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.jwt.JwtService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers controller", description = "Controller class for customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final JwtService jwtService;

    @PostMapping("/save")
    @Operation(summary = "Save customer", description = "Save method for customer")
    public ResponseEntity<Map<String, String>> saveCustomer(@RequestBody @Validated(ValidationGroup.Save.class) CustomerSaveUpdateRequest customer) {
        Customer register = customerService.register(customer);
        String token = jwtService.generateToken(new UserDetailsImpl(register));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping("/update")
    @Operation(summary = "Update customer", description = "Update method for customer")
    public ResponseEntity<CustomerFindResponse> updateCustomer(@RequestBody @Validated(ValidationGroup.Update.class) CustomerSaveUpdateRequest customer, HttpSession session) {
        Customer updated = customerService.updateWithDTO(customer);
        session.setAttribute("currentUser", new UserSessionDTO(updated.getId(), updated.getEmail(), Role.ROLE_CUSTOMER));
        return ResponseEntity.ok(customerMapper.mapToResponse(updated));
    }

    @GetMapping("/find-by-email")
    @Operation(summary = "Find by email", description = "Find a customer with email")
    public ResponseEntity<CustomerFindResponse> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(customerMapper.mapToResponse(customerService.findByEmail(email)));
    }
}
