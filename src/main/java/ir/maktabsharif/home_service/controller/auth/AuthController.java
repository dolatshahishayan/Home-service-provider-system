package ir.maktabsharif.home_service.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth controller",description = "Authentication controller")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Login",description = "Login method")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(token);
        if (authenticate == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Unauthorized");
        }
        UserDetailsImpl principal = (UserDetailsImpl) authenticate.getPrincipal();
        String jwtToken = jwtUtil.generateToken(principal);
        response.addHeader("Authorization", "Bearer " + jwtToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        return ResponseEntity.ok("Token: "+jwtToken);
    }
}
