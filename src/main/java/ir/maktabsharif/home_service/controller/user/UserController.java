package ir.maktabsharif.home_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.service.jwt.JwtService;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users controller", description = "Controller class for users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login method for user")
    public ResponseEntity<Map<String,String>> login(@RequestBody @Validated LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );
        User user = userService.findByEmail(loginDTO.getEmail());
        String token = jwtService.generateToken(new UserDetailsImpl(user));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout method for user")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

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

    @GetMapping("/search-users")
    @Operation(summary = "Search users", description = "Search users by role, first name or last name, service and score interval")
    public ResponseEntity<List<UserSearchResponseDTO>> searchUsers(@RequestBody @Validated UserSearchRequestDTO userSearchRequestDTO) {
        return ResponseEntity.ok(userService.searchUsers(userSearchRequestDTO));
    }
}
