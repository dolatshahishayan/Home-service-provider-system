package ir.maktabsharif.home_service.service.transaction;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.dto.transaction.TransactionInitializerDTO;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService extends BaseService<Transaction, Integer> {
    void saveTransaction(Transaction transaction);
    Page<TransactionFindResponse> findByUserId(Pageable pageable);
    TransactionInitializerDTO createPendingTransaction();
}
