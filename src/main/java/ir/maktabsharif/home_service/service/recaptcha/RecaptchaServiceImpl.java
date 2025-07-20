package ir.maktabsharif.home_service.service.recaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean isValid(String token) {
        System.out.println("⚡️ Recaptcha validation started...");
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
        Map<String, Object> body = getStringObjectMap(token, verifyUrl);
        if (body == null || !Boolean.TRUE.equals(body.get("success"))) {
            return false;
        }
        Double score = (Double) body.get("score");
        String action = (String) body.get("action");
        return score != null && score >= 0.5 && "submit".equals(action);
    }

    private Map<String, Object> getStringObjectMap(String token, String verifyUrl) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(verifyUrl, HttpMethod.POST, request, new ParameterizedTypeReference<>() {});
        Map<String, Object> body = response.getBody();
        return body;
    }
}
