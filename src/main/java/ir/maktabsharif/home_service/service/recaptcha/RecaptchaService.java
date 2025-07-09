package ir.maktabsharif.home_service.service.recaptcha;

public interface RecaptchaService {
    boolean isValid(String token);
}
