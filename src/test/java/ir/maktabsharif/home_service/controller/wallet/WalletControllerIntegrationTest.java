package ir.maktabsharif.home_service.controller.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.payment.PaymentRequestDTO;
import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.JwtUtil;
import ir.maktabsharif.home_service.util.RecaptchaUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RecaptchaUtil recaptchaUtil;

    private String customerToken;
    private Wallet wallet;
    private WalletFindResponse walletFindResponse;
    private User save;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setPassword(passwordEncoder.encode("test"));
        user3.setIsEmailVerified(true);
        user3.setRole(Role.ROLE_CUSTOMER);
        save = userService.save(user3);
        UserDetails userDetails3 = userService.loadUserByUsername(user3.getEmail());
        customerToken = jwtUtil.generateToken(userDetails3);
        wallet = new Wallet(BigDecimal.valueOf(100.0), user3);
        wallet.setId(1);
        walletFindResponse = new WalletFindResponse(1, 100.0, 1);

    }

    @AfterAll
    void deleteUsers() {
        userService.deleteAll();
    }

    @Test
    void saveWallet_ShouldReturnSavedWallet() throws Exception {
        WalletSaveUpdateRequest request = new WalletSaveUpdateRequest(null, 100.0, 1);

        Mockito.when(walletService.saveWithDTO(any(WalletSaveUpdateRequest.class))).thenReturn(wallet);
        Mockito.when(walletMapper.mapToResponse(wallet)).thenReturn(walletFindResponse);

        mockMvc.perform(post("/api/v1/wallets/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void addCreditToWallet_ShouldReturnSuccessMessage() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                123,
                50.0,
                "4111111111111111",
                "123",
                "12/25",
                "000000",
                "captcha",
                10
        );

        Mockito.when(recaptchaUtil.isValid(anyString())).thenReturn(true);

        mockMvc.perform(put("/api/v1/wallets/add-credit-to-wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Added credit to wallet"));

        Mockito.verify(walletService).addCreditToWallet(eq(50.0), eq(123), eq(save.getId()));
    }

    @Test
    void addCreditToWallet_WithInvalidCaptcha_ShouldReturnForbidden() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                123,
                50.0,
                "4111111111111111",
                "123",
                "12/25",
                "000000",
                "badCaptcha",
                10
        );

        Mockito.when(recaptchaUtil.isValid(anyString())).thenReturn(false);

        mockMvc.perform(put("/api/v1/wallets/add-credit-to-wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Captcha is not valid"));
    }

    @Test
    void addCreditToWallet_WithInvalidTime_ShouldReturnForbidden() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                123,
                50.0,
                "4111111111111111",
                "123",
                "12/25",
                "000000",
                "captcha",
                0
        );

        Mockito.when(recaptchaUtil.isValid(anyString())).thenReturn(true);

        mockMvc.perform(put("/api/v1/wallets/add-credit-to-wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Client time left is invalid"));
    }


    @Test
    void findByUserId_ShouldReturnWallet() throws Exception {
        Mockito.when(walletService.findByUserId(1)).thenReturn(wallet);
        Mockito.when(walletMapper.mapToResponse(wallet)).thenReturn(walletFindResponse);

        mockMvc.perform(get("/api/v1/wallets/find-by-user-id")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void payFromWallet_ShouldReturnWallet() throws Exception {
        Mockito.when(walletService.payFromWallet(1)).thenReturn(wallet);
        Mockito.when(walletMapper.mapToResponse(wallet)).thenReturn(walletFindResponse);

        mockMvc.perform(put("/api/v1/wallets/pay-from-wallet")
                        .param("orderId", "1")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void getCurrentBalance_ShouldReturnBalance() throws Exception {
        Mockito.when(walletService.getCurrentBalance(save.getId())).thenReturn(500.0);

        mockMvc.perform(get("/api/v1/wallets/get-balance")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("500.0"));
    }
}
