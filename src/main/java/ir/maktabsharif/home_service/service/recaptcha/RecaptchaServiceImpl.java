package ir.maktabsharif.home_service.service.recaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {
//add value annotation and add secret key
    private String recaptchaSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean isValid(String token) {
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", token);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                verifyUrl,
                HttpMethod.POST,
                new HttpEntity<>(params),
                new ParameterizedTypeReference<>() {
                }
        );


        Map<String, Object> body = response.getBody();

        if (body != null && body.get("success") instanceof Boolean success) {
            return success;
        }

        return false;
    }
}
