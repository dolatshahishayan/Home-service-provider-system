package ir.maktabsharif.home_service.util;

import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.token.EmailVerificationTokenRepository;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@Transactional
public class EmailUtil {
    private final JavaMailSender mailSender;
    @Value("${from.email}")
    private String from;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserService userService;
    private final ExpertService expertService;
    public EmailUtil(JavaMailSender mailSender, EmailVerificationTokenRepository emailVerificationTokenRepository, UserService userService,@Lazy ExpertService expertService) {
        this.mailSender = mailSender;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userService = userService;
        this.expertService = expertService;
    }

    public void sendVerificationEmail(String to, String firstName, String verificationLink){
        String subject = "Confirm Your Account";
        String text = "Hello " + firstName + ",\n\n" +
                "Thank you for registering.\n" +
                "Please confirm your account by clicking the link below:\n\n" +
                verificationLink + "\n\n" +
                "If you didn’t register, please ignore this email.\n\n" +
                "Regards,\nMaktab sharif";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void deleteAll(){
        emailVerificationTokenRepository.deleteAll();
    }

    public String buildFrontendVerificationLink(EmailVerificationToken token) {
        return frontendUrl + "/verify-email.html?token=" + token.getToken();
    }

    public EmailVerificationToken createToken(User user, int minutesValid) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(minutesValid));
        return emailVerificationTokenRepository.save(token);
    }

    public void verifyToken(String tokenValue) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(NoElementFoundException::new);
        if (token.getUsed()) {
            throw new InvalidRequestException("Token already used");
        }
        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            throw new InvalidRequestException("Token expired");
        }
        setUserEmailToVerified(token);
        token.setUsed(true);
        emailVerificationTokenRepository.save(token);
    }

    private void setUserEmailToVerified(EmailVerificationToken token) {
        User user = token.getUser();
        user.setIsEmailVerified(true);
        if (user instanceof Expert e){

                e.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
                expertService.save(e);
                return;

        }
        userService.save(user);
    }
}
