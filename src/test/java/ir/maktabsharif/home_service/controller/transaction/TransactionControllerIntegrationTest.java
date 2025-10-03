package ir.maktabsharif.home_service.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.dto.transaction.TransactionInitializerDTO;
import ir.maktabsharif.home_service.model.enums.TransactionStatus;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TransactionControllerIntegrationTest.MockConfig.class)
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SecurityContextUtil securityContextUtil;

    @Autowired
    private ObjectMapper objectMapper;

    static class MockConfig {
        @Bean
        TransactionService transactionService() {
            return Mockito.mock(TransactionService.class);
        }

        @Bean
        SecurityContextUtil securityContextUtil() {
            return Mockito.mock(SecurityContextUtil.class);
        }
    }

    @BeforeEach
    void setup() {
        User mockUser = new User();
        mockUser.setId(1);

        UserDetailsImpl userDetails = new UserDetailsImpl(mockUser);
        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(userDetails);
    }

    @Test
    void findTransactionsByUser_ShouldReturnPagedTransactions() throws Exception {
        TransactionFindResponse transactionResponse = new TransactionFindResponse(
                100.0, 1, 2, LocalDateTime.now(), TransactionStatus.COMPLETED
        );

        Page<TransactionFindResponse> page = new PageImpl<>(List.of(transactionResponse));

        Mockito.when(transactionService.findByUserId(any(PageRequest.class), eq(1)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/transactions/find-by-user")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveInitialTransaction_ShouldReturnInitializerDTO() throws Exception {
        TransactionInitializerDTO initializerDTO = new TransactionInitializerDTO(
                1, LocalDateTime.now().plusMinutes(15)
        );

        Mockito.when(transactionService.createPendingTransaction(eq(1)))
                .thenReturn(initializerDTO);

        mockMvc.perform(post("/api/v1/transactions/save-initial-transaction")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(initializerDTO)));
    }

}
