package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.model.transaction.Transaction;

import java.util.List;

public interface TransactionService extends BaseService<Transaction, Integer> {
    void saveTransaction(Transaction transaction);
    List<Transaction> findBySenderId(Integer customerId);
    List<Transaction> findByReceiverId(Integer customerId);
}
