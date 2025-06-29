package ir.maktabsharif.home_service.repository.wallet;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.wallet.Wallet;

public interface WalletRepository extends CrudRepository<Wallet> {
    Wallet findByUserId(Integer userId);
}
