package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.dto.transaction.TransactionInitializerDTO;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.transaction.TransactionMapper;
import ir.maktabsharif.home_service.model.enums.TransactionStatus;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.transaction.TransactionRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionServiceImpl extends BaseServiceImpl<Transaction, Integer, TransactionRepository, TransactionMapper> implements TransactionService {
    protected final UserService userService;

    public TransactionServiceImpl(TransactionRepository repository, TransactionMapper transactionMapper, UserService userService) {
        super(repository, transactionMapper);
        this.userService = userService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTransaction(Transaction transaction) {
        if (transaction.getSender() == null || transaction.getReceiver() == null || transaction.getAmount() == null) {
            throw new InvalidRequestException("Transaction must have sender, receiver, and amount");
        }
        transaction.setTimestamp(LocalDateTime.now());
        save(transaction);
    }

    @Override
    public Page<TransactionFindResponse> findByUserId(Pageable pageable,Integer userId) {
        User principal = userService.findById(userId);
        Page<Transaction> bySenderIdOrReceiverId = repository.findBySenderIdOrReceiverId(principal.getId(), principal.getId(), pageable);
        if (bySenderIdOrReceiverId.getContent().isEmpty()) {
            throw new NoElementFoundException();
        }
        return bySenderIdOrReceiverId.map(mapper::mapToResponse);
    }

    @Override
    public TransactionInitializerDTO createPendingTransaction(Integer userId) {
        User principal = userService.findById(userId);
        Transaction transaction = new Transaction();
        transaction.setSender(principal);
        transaction.setReceiver(principal);
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setExpireDate(LocalDateTime.now().plusMinutes(10));
        Transaction save = save(transaction);
        return new TransactionInitializerDTO(save.getId(),save.getExpireDate());
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
