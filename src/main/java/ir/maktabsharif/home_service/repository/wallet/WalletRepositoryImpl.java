package ir.maktabsharif.home_service.repository.wallet;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WalletRepositoryImpl extends CrudRepositoryImpl<Wallet> implements WalletRepository {

    public WalletRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Wallet> getEntityClass() {
        return Wallet.class;
    }

    @Override
    public Wallet findByUserId(Integer userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Wallet> cq = cb.createQuery(Wallet.class);
        Root<Wallet> root = cq.from(Wallet.class);
        cq.select(root).where(cb.equal(root.get("userId"), userId));
        List<Wallet> wallets = em.createQuery(cq).getResultList();
        return wallets.getFirst();
    }
}
