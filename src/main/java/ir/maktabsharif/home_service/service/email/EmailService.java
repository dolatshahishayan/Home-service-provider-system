package ir.maktabsharif.home_service.service.email;

import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import ir.maktabsharif.home_service.model.user.User;

public interface EmailService {
    void sendVerificationEmail(String to, String subject, String text);

    String buildFrontendVerificationLink(EmailVerificationToken token);

    EmailVerificationToken createToken(User user, int minutesValid);

    void verifyToken(String tokenValue);
}
