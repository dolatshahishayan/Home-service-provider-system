package ir.maktabsharif.home_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User controller", description = "controller class for user")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login method for user")
    public ResponseEntity<String> login(@RequestBody @Validated LoginDTO loginDTO, HttpSession session) {
        User byEmailAndPassword = userService.findByEmailAndPassword(loginDTO);
        session.setAttribute("currentUser", new UserSessionDTO(byEmailAndPassword.getId(), byEmailAndPassword.getEmail(), byEmailAndPassword.getRole()));
        return ResponseEntity.ok("Login successful");
    }
    @PostMapping("/logout")
    @Operation(summary = "User logout",description = "Logout method for user")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/exists-by-email")
    @Operation(summary = "Exists by email",description = "Checks if a user exists by email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @GetMapping("/exists-by-email-and-id-not")
    @Operation(summary = "Exists by email and id not",description = "Checks if a user exists with by email and it doesn't check the user with the given id")
    public ResponseEntity<Boolean> existsByEmailAndIdNot(@RequestParam String email, @RequestParam Integer id) {
        return ResponseEntity.ok(userService.existsByEmailAndIdNot(email, id));
    }
}
