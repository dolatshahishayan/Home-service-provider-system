package ir.maktabsharif.home_service.repository.expert;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpertRepositoryImpl extends CrudRepositoryImpl<Expert> implements ExpertRepository {
    public ExpertRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Expert> getEntityClass() {
        return Expert.class;
    }

    @Override
    public Expert findByEmailAndPassword(String email,String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Expert> cq = cb.createQuery(Expert.class);
        Root<Expert> root = cq.from(Expert.class);
        cq.select(root).where(cb.and(cb.equal(root.get("email"), email)),cb.equal(root.get("password"), password));
        TypedQuery<Expert> query = em.createQuery(cq);
        List<Expert> experts = query.getResultList();
        return experts.isEmpty() ? null : experts.getFirst();
    }
}
