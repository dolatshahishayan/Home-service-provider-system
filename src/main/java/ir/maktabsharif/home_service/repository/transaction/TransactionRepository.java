package ir.maktabsharif.home_service.repository.transaction;

import ir.maktabsharif.home_service.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> , JpaSpecificationExecutor<Transaction> {
    List<Transaction> findBySenderId(Integer customerId);
    List<Transaction> findByReceiverId(Integer customerId);
}
