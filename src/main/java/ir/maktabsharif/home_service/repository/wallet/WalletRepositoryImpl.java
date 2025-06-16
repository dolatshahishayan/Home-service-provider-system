package ir.maktabsharif.home_service.repository.wallet;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRepositoryImpl extends CrudRepositoryImpl<Wallet> implements WalletRepository {
    public WalletRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Wallet> getEntityClass() {
        return Wallet.class;
    }
}
