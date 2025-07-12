package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.transaction.TransactionMapper;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.transaction.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    @Test
    void saveTransaction_shouldSetTimestampAndSave() {
        Transaction transaction = new Transaction();
        transaction.setSender(new User());
        transaction.setReceiver(new User());
        transaction.setAmount(100.0);

        transactionService.saveTransaction(transaction);

        assertThat(transaction.getTimestamp()).isNotNull();
        verify(repository).save(transaction);
    }


    @Test
    void saveTransaction_shouldThrowException_whenSenderOrReceiverOrAmountIsNull() {
        Transaction transaction = new Transaction();

        assertThatThrownBy(() -> transactionService.saveTransaction(transaction))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Transaction must have sender, receiver, and amount");
    }

    @Test
    void findByUserId_ShouldReturnMappedTransactions_WhenExists() {
        Integer userId = 1;

        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();

        TransactionFindResponse response1 = new TransactionFindResponse();
        TransactionFindResponse response2 = new TransactionFindResponse();

        when(repository.findBySenderIdOrReceiverId(userId, userId))
                .thenReturn(List.of(transaction1, transaction2));
        when(mapper.mapToResponse(transaction1)).thenReturn(response1);
        when(mapper.mapToResponse(transaction2)).thenReturn(response2);

        List<TransactionFindResponse> result = transactionService.findByUserId(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
        verify(repository).findBySenderIdOrReceiverId(userId, userId);
        verify(mapper).mapToResponse(transaction1);
        verify(mapper).mapToResponse(transaction2);
    }

    @Test
    void findByUserId_ShouldThrow_WhenNoTransactionFound() {
        Integer userId = 2;

        when(repository.findBySenderIdOrReceiverId(userId, userId))
                .thenReturn(Collections.emptyList());

        assertThrows(NoElementFoundException.class, () -> transactionService.findByUserId(userId));

        verify(repository).findBySenderIdOrReceiverId(userId, userId);
        verify(mapper, never()).mapToResponse(any());
    }

}
