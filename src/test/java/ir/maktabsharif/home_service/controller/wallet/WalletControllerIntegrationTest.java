package ir.maktabsharif.home_service.controller.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.payment.PaymentRequestDTO;
import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.RecaptchaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
    private SecurityContextUtil securityContextUtil;

    @Autowired
    private RecaptchaUtil recaptchaUtil;



    private Wallet wallet;
    private WalletFindResponse walletFindResponse;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);
        wallet = new Wallet(BigDecimal.valueOf(100.0), user);
        wallet.setId(1);
        walletFindResponse = new WalletFindResponse(1, 100.0, 1);

        UserDetailsImpl principal = new UserDetailsImpl(user);
        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(principal);
    }

    @Test
    void saveWallet_ShouldReturnSavedWallet() throws Exception {
        WalletSaveUpdateRequest request = new WalletSaveUpdateRequest(null, 100.0, 1);

        Mockito.when(walletService.saveWithDTO(any(WalletSaveUpdateRequest.class))).thenReturn(wallet);
        Mockito.when(walletMapper.mapToResponse(wallet)).thenReturn(walletFindResponse);

        mockMvc.perform(post("/api/v1/wallets/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Added credit to wallet"));

        Mockito.verify(walletService).addCreditToWallet(eq(50.0), eq(123), eq(1));
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
                        .content(objectMapper.writeValueAsString(requestDTO)))
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
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Client time left is invalid"));
    }


    @Test
    void findByUserId_ShouldReturnWallet() throws Exception {
        Mockito.when(walletService.findByUserId(1)).thenReturn(wallet);
        Mockito.when(walletMapper.mapToResponse(wallet)).thenReturn(walletFindResponse);

        mockMvc.perform(get("/api/v1/wallets/find-by-user-id")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
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
                        .param("orderId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void getCurrentBalance_ShouldReturnBalance() throws Exception {
        Mockito.when(walletService.getCurrentBalance(1)).thenReturn(500.0);

        mockMvc.perform(get("/api/v1/wallets/get-balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("500.0"));
    }
}
