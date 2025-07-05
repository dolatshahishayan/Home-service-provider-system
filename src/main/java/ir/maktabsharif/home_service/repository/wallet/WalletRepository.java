package ir.maktabsharif.home_service.repository.wallet;

import ir.maktabsharif.home_service.model.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer>, JpaSpecificationExecutor<Wallet> {
    Optional<Wallet> findByUserId(Integer userId);
}
