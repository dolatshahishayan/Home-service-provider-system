package ir.maktabsharif.home_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.PagedResponse;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users controller", description = "Controller class for users")
public class UserController {
    private final UserService userService;

    @GetMapping("/exists-by-email")
    @Operation(summary = "Exists by email", description = "Checks if a user exists by email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @GetMapping("/exists-by-email-and-id-not")
    @Operation(summary = "Exists by email and id not", description = "Checks if a user exists with by email and it doesn't check the user with the given id")
    public ResponseEntity<Boolean> existsByEmailAndIdNot(@RequestParam String email, @RequestParam Integer id) {
        return ResponseEntity.ok(userService.existsByEmailAndIdNot(email, id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/search-users")
    @Operation(summary = "Search users", description = "Search users by role, first name or last name, service and score interval")
    public ResponseEntity<PagedResponse<UserSearchResponseDTO>> searchUsers(@RequestBody @Validated UserSearchRequestDTO userSearchRequestDTO,  @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<UserSearchResponseDTO> results = userService.searchUsers(userSearchRequestDTO, PageRequest.of(page, size));
        return ResponseEntity.ok(new PagedResponse<>(
                results.getContent(),
                results.getNumber(),
                results.getSize(),
                results.getTotalElements()
        ));
    }
}
