package ir.maktabsharif.home_service;

import ch.qos.logback.core.testUtil.MockInitialContext;
import ir.maktabsharif.home_service.controller.auth.AuthController;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.mapper.expert_service.ExpertServiceMapper;
import ir.maktabsharif.home_service.mapper.order.OrderMapper;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.security.JwtAuthenticationProvider;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.comment.CommentService;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.EmailUtil;
import ir.maktabsharif.home_service.util.JwtUtil;
import ir.maktabsharif.home_service.util.RecaptchaUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@TestConfiguration
public class TestMockConfig {
    @Bean
    public JwtUtil mockJwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }
    @Bean
    CommentService commentService() {
        return Mockito.mock(CommentService.class);
    }

    @Bean
    CommentMapper commentMapper() {
        return Mockito.mock(CommentMapper.class);
    }

    @Bean
    SecurityContextUtil securityContextUtil() {
        return Mockito.mock(SecurityContextUtil.class);
    }
    @Bean
    CustomerService customerService() {
        return Mockito.mock(CustomerService.class);
    }

    @Bean
    CustomerMapper customerMapper() {
        return Mockito.mock(CustomerMapper.class);
    }

    @Bean
    ExpertService expertService() {
        return Mockito.mock(ExpertService.class);
    }

    @Bean
    ExpertMapper expertMapper() {
        return Mockito.mock(ExpertMapper.class);
    }
    @Bean
    ExpertServiceService expertServiceService() {
        return Mockito.mock(ExpertServiceService.class);
    }

    @Bean
    ExpertServiceMapper expertServiceMapper() {
        return Mockito.mock(ExpertServiceMapper.class);
    }
    @Bean
    OrderService orderService() {
        return Mockito.mock(OrderService.class);
    }

    @Bean
    OrderMapper orderMapper() {
        return Mockito.mock(OrderMapper.class);
    }
    @Bean
    ServiceService serviceService() {
        return Mockito.mock(ServiceService.class);
    }

    @Bean
    ServiceMapper serviceMapper() {
        return Mockito.mock(ServiceMapper.class);
    }
    @Bean
    SuggestionService suggestionService() {
        return Mockito.mock(SuggestionService.class);
    }

    @Bean
    SuggestionMapper suggestionMapper() {
        return Mockito.mock(SuggestionMapper.class);
    }
    @Bean
    EmailUtil emailUtil() {
        return Mockito.mock(EmailUtil.class);
    }
    @Bean
    TransactionService transactionService() {
        return Mockito.mock(TransactionService.class);
    }


    @Bean
    RecaptchaUtil recaptchaUtil() {
        return Mockito.mock(RecaptchaUtil.class);
    }
    @Bean
    WalletService walletService() {
        return Mockito.mock(WalletService.class);
    }

    @Bean
    WalletMapper walletMapper() {
        return Mockito.mock(WalletMapper.class);
    }

    @Bean
    HttpServletResponse httpServletResponse(){
        return Mockito.mock(HttpServletResponse.class);
    }

}
