package ir.maktabsharif.home_service.repository.suggestion;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SuggestionRepositoryImpl extends CrudRepositoryImpl<Suggestion> implements SuggestionRepository {
    public SuggestionRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Suggestion> getEntityClass() {
        return Suggestion.class;
    }


    @Override
    public List<Suggestion> findAllByExpertId(Integer expertId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Suggestion> cq = cb.createQuery(Suggestion.class);
        Root<Suggestion> from = cq.from(Suggestion.class);
        cq.select(from);
        cq.where(cb.equal(from.get("expertId"), expertId));
        return em.createQuery(cq).getResultList();
    }
}
