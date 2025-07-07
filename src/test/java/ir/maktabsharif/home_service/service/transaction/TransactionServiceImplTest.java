package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.transaction.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

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
    void findBySenderId_shouldReturnTransactionList() {
        Integer senderId = 1;
        List<Transaction> mockTransactions = List.of(new Transaction(), new Transaction());
        when(repository.findBySenderId(senderId)).thenReturn(mockTransactions);

        List<Transaction> result = transactionService.findBySenderId(senderId);

        assertThat(result).hasSize(2);
        verify(repository).findBySenderId(senderId);
    }

    @Test
    void findByReceiverId_shouldReturnTransactionList() {
        Integer receiverId = 2;
        List<Transaction> mockTransactions = List.of(new Transaction());
        when(repository.findByReceiverId(receiverId)).thenReturn(mockTransactions);

        List<Transaction> result = transactionService.findByReceiverId(receiverId);

        assertThat(result).hasSize(1);
        verify(repository).findByReceiverId(receiverId);
    }

    @Test
    void saveTransaction_shouldThrowException_whenSenderOrReceiverOrAmountIsNull() {
        Transaction transaction = new Transaction();

        assertThatThrownBy(() -> transactionService.saveTransaction(transaction))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Transaction must have sender, receiver, and amount");
    }
}
