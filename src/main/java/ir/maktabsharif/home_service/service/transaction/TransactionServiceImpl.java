package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
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
        transaction.setTimestamp(LocalDateTime.now());
        save(transaction);
    }

    @Override
    public List<Transaction> findBySenderId(Integer customerId) {
        return repository.findBySenderId(customerId);
    }

    @Override
    public List<Transaction> findByReceiverId(Integer customerId) {
        return repository.findByReceiverId(customerId);
    }
}
