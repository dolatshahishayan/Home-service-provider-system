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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User controller", description = "controller class for user")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "user login", description = "login method for user")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        User byEmailAndPassword = userService.findByEmailAndPassword(loginDTO);
        session.setAttribute("currentUser", new UserSessionDTO(byEmailAndPassword.getId(), byEmailAndPassword.getEmail(), byEmailAndPassword.getRole()));
        return ResponseEntity.ok("Login successful");
    }
    @PostMapping("/logout")
    @Operation(summary = "user logout",description = "logout method for user")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

}
