package ir.maktabsharif.home_service.controller.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.payment.PaymentRequestDTO;
import ir.maktabsharif.home_service.dto.transaction.TransactionInitializerDTO;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.service.ServiceService;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.JwtUtil;
import ir.maktabsharif.home_service.util.RecaptchaUtil;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestMockConfig.class)
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RecaptchaUtil recaptchaUtil;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ExpertService expertService;

    private String customerToken;
    private Customer save;
    private TransactionInitializerDTO pendingTransaction;
    private Wallet save2;
    private Order orderTest;
    private Customer save3;

    @BeforeEach
    void setup() {
        userService.deleteAll();
        Customer user3 = new Customer();
        user3.setEmail("user3@test.com");
        user3.setPassword(passwordEncoder.encode("test"));
        user3.setIsEmailVerified(true);
        user3.setRole(Role.ROLE_CUSTOMER);
        save = customerService.save(user3);
        UserDetails userDetails3 = userService.loadUserByUsername(user3.getEmail());
        customerToken = jwtUtil.generateToken(userDetails3);

        Customer user4 = new Customer();
        user4.setEmail("user5@test.com");
        user4.setPassword(passwordEncoder.encode("test"));
        user4.setIsEmailVerified(true);
        user4.setRole(Role.ROLE_CUSTOMER);
        save3 = customerService.save(user4);
        Expert user = new Expert();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("test"));
        user.setIsEmailVerified(true);
        user.setRole(Role.ROLE_EXPERT);
        user.setExpertStatus(ExpertStatus.VERIFIED);
        Expert expertTest = expertService.save(user);

        pendingTransaction = transactionService.createPendingTransaction(save.getId());

        Wallet wallet = new Wallet();
        wallet.setUser(save);
        wallet.setBalance(BigDecimal.ZERO);
        save2 = walletService.save(wallet);
        Wallet wallet2 = new Wallet();
        wallet2.setUser(expertTest);
        wallet2.setBalance(BigDecimal.ZERO);
        walletService.save(wallet2);
        Service serviceEntity = new Service();
        serviceEntity.setName("Test Service");
        serviceEntity.setBasePrice(BigDecimal.valueOf(100.0));
        serviceEntity.setDescription("Test Description");
        serviceEntity = serviceService.save(serviceEntity);

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);
        order.setCustomer(save);
        order.setExpert(expertTest);
        order.setDescription("Painting");
        order.setService(serviceEntity);
        order.setFinalPrice(BigDecimal.valueOf(0));
        order.setProposedPrice(BigDecimal.valueOf(0));
        orderTest = orderService.save(order);
    }

    @AfterEach
    void deleteUsers() {
        orderService.deleteAll();
        serviceService.deleteAll();
        transactionService.deleteAll();
        walletService.deleteAll();
        customerService.deleteAll();
        expertService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void saveWallet_ShouldReturnSavedWallet() throws Exception {
        WalletSaveUpdateRequest request = new WalletSaveUpdateRequest(null, 100.0, save3.getId());

        mockMvc.perform(post("/api/v1/wallets/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(save3.getId()));
    }

    @Test
    void addCreditToWallet_ShouldReturnSuccessMessage() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(
                pendingTransaction.getId(),
                50.0,
                "4111111111111111",
                "123",
                "12/25",
                "000000",
                "captcha",
                10
        );
        Mockito.when(recaptchaUtil.isValid(requestDTO.getRecaptcha())).thenReturn(true);

        mockMvc.perform(put("/api/v1/wallets/add-credit-to-wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Added credit to wallet"));
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

        Mockito.when(recaptchaUtil.isValid(requestDTO.getRecaptcha())).thenReturn(false);

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
                "badCaptcha",
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
        String id = String.valueOf(save.getId());
        mockMvc.perform(get("/api/v1/wallets/find-by-user-id")
                        .param("userId", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(save2.getId()))
                .andExpect(jsonPath("$.userId").value(save.getId()));
    }

    @Test
    void payFromWallet_ShouldReturnWallet() throws Exception {
        String id = String.valueOf(orderTest.getId());

        mockMvc.perform(put("/api/v1/wallets/pay-from-wallet")
                        .param("orderId", id)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(save2.getId()))
                .andExpect(jsonPath("$.userId").value(save.getId()));
    }

    @Test
    void getCurrentBalance_ShouldReturnBalance() throws Exception {

        mockMvc.perform(get("/api/v1/wallets/get-balance")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk());
    }
}
