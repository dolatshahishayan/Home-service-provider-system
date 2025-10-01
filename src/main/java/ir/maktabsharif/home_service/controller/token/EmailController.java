package ir.maktabsharif.home_service.controller.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.VerificationRequest;
import ir.maktabsharif.home_service.util.EmailUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication controller",description = "Controller class for authentication")
public class EmailController {

    private final EmailUtil emailUtil;

    public EmailController(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email",description = "Method for verifying email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerificationRequest verificationRequest) {
        emailUtil.verifyToken(verificationRequest.getToken());
        return ResponseEntity.ok("Email verified");
    }
}
