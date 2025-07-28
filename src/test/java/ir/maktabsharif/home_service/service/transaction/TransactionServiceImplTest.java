package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.dto.transaction.TransactionInitializerDTO;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.transaction.TransactionMapper;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.transaction.TransactionRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionMapper transactionMapper;

    @Mock
    UserService userService;

    @Test
    void saveTransaction_success() {
        User sender = new User();
        User receiver = new User();
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(100.0);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.saveTransaction(transaction);

        assertNotNull(transaction.getTimestamp());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void saveTransaction_missingSender_throws() {
        Transaction transaction = new Transaction();
        transaction.setReceiver(new User());
        transaction.setAmount(100.0);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> transactionService.saveTransaction(transaction));
        assertEquals("Transaction must have sender, receiver, and amount", ex.getMessage());
    }

    @Test
    void saveTransaction_missingReceiver_throws() {
        Transaction transaction = new Transaction();
        transaction.setSender(new User());
        transaction.setAmount(100.0);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> transactionService.saveTransaction(transaction));
        assertEquals("Transaction must have sender, receiver, and amount", ex.getMessage());
    }

    @Test
    void saveTransaction_missingAmount_throws() {
        Transaction transaction = new Transaction();
        transaction.setSender(new User());
        transaction.setReceiver(new User());

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () -> transactionService.saveTransaction(transaction));
        assertEquals("Transaction must have sender, receiver, and amount", ex.getMessage());
    }

    @Test
    void findByUserId_success() {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Transaction transaction = new Transaction();
        transaction.setSender(user);

        Page<Transaction> page = new PageImpl<>(List.of(transaction));
        Pageable pageable = Pageable.unpaged();

        when(userService.findById(userId)).thenReturn(user);
        when(transactionRepository.findBySenderIdOrReceiverId(userId, userId, pageable)).thenReturn(page);
        when(transactionMapper.mapToResponse(transaction)).thenReturn(mock(TransactionFindResponse.class));

        Page<TransactionFindResponse> responses = transactionService.findByUserId(pageable, userId);

        assertFalse(responses.isEmpty());
        verify(transactionRepository).findBySenderIdOrReceiverId(userId, userId, pageable);
    }

    @Test
    void findByUserId_noElements_throws() {
        int userId = 1;
        User user = new User();
        user.setId(userId);

        Pageable pageable = Pageable.unpaged();

        when(userService.findById(userId)).thenReturn(user);
        when(transactionRepository.findBySenderIdOrReceiverId(userId, userId, pageable)).thenReturn(Page.empty());

        assertThrows(NoElementFoundException.class, () -> transactionService.findByUserId(pageable, userId));
    }

    @Test
    void createPendingTransaction_success() {
        int userId = 1;
        User user = new User();
        user.setId(userId);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(10);
        savedTransaction.setExpireDate(LocalDateTime.now().plusMinutes(10));

        when(userService.findById(userId)).thenReturn(user);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionInitializerDTO dto = transactionService.createPendingTransaction(userId);

        assertEquals(savedTransaction.getId(), dto.getId());
        assertEquals(savedTransaction.getExpireDate(), dto.getExpireDate());
        verify(transactionRepository).save(any(Transaction.class));
    }
}
