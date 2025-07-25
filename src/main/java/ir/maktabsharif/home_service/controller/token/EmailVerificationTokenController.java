package ir.maktabsharif.home_service.controller.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.VerificationRequest;
import ir.maktabsharif.home_service.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication controller",description = "Controller class for authentication")
public class EmailVerificationTokenController {

    private final EmailUtil emailUtil;

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email",description = "Method for verifying email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerificationRequest verificationRequest) {
        emailUtil.verifyToken(verificationRequest.getToken());
        return ResponseEntity.ok("Email verified");
    }
}
