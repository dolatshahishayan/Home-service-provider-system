package ir.maktabsharif.home_service.util;

import ir.maktabsharif.home_service.util.JwtUtil;
import ir.maktabsharif.home_service.util.KeyUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class JwtConfig {

    @Bean
    public PrivateKey privateKey() throws Exception {
        return KeyUtil.getPrivateKey("C:\\Users\\Shayan\\IdeaProjects\\Home_Service\\src\\main\\resources\\private_key.pem");
    }

    @Bean
    public PublicKey publicKey() throws Exception {
        return KeyUtil.getPublicKey("C:\\Users\\Shayan\\IdeaProjects\\Home_Service\\src\\main\\resources\\public_key.pem");
    }

    @Bean
    public JwtUtil jwtUtil(PrivateKey privateKey, PublicKey publicKey, UserDetailsService userDetailsService) {
        return new JwtUtil(privateKey, publicKey, userDetailsService);
    }
}
