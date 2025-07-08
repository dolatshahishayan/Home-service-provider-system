package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.transaction.TransactionMapper;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.repository.transaction.TransactionRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Transactional
public class TransactionServiceImpl extends BaseServiceImpl<Transaction, Integer, TransactionRepository, TransactionMapper> implements TransactionService {
    protected final UserService userService;

    public TransactionServiceImpl(TransactionRepository repository, TransactionMapper transactionMapper, UserService userService) {
        super(repository, transactionMapper);
        this.userService = userService;
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction.getSender() == null || transaction.getReceiver() == null || transaction.getAmount() == null) {
            throw new InvalidRequestException("Transaction must have sender, receiver, and amount");
        }
        transaction.setTimestamp(LocalDateTime.now());
        save(transaction);
    }

    @Override
    public List<Transaction> findBySenderId(Integer customerId) {
        List<Transaction> bySenderId = repository.findBySenderId(customerId);
        if (bySenderId.isEmpty()) {
            throw new NoElementFoundException();
        }
        return bySenderId;
    }

    @Override
    public List<Transaction> findByReceiverId(Integer customerId) {
        List<Transaction> byReceiverId = repository.findByReceiverId(customerId);
        if (byReceiverId.isEmpty()) {
            throw new NoElementFoundException();
        }
        return byReceiverId;
    }
}
