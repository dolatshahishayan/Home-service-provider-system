package ir.maktabsharif.home_service.dto.user;

public class VerificationRequest {
    private String token;

    public VerificationRequest() {
    }

    public VerificationRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
