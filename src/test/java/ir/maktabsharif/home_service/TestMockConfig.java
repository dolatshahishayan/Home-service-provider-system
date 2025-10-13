package ir.maktabsharif.home_service;

import ir.maktabsharif.home_service.util.RecaptchaUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMockConfig {


    @Bean
    RecaptchaUtil recaptchaUtil() {
        return Mockito.mock(RecaptchaUtil.class);
    }


}
