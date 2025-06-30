package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpertServiceRepositoryImpl extends CrudRepositoryImpl<ExpertService> implements ExpertServiceRepository {

    public ExpertServiceRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<ExpertService> getEntityClass() {
        return ExpertService.class;
    }

    @Override
    public ExpertService findByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExpertService> query = cb.createQuery(ExpertService.class);
        Root<ExpertService> root = query.from(ExpertService.class);

        Predicate expertPredicate = cb.equal(root.get("expert").get("id"), expertId);
        Predicate servicePredicate = cb.equal(root.get("service").get("id"), serviceId);

        query.select(root).where(cb.and(expertPredicate, servicePredicate));

        TypedQuery<ExpertService> typedQuery = em.createQuery(query);
        List<ExpertService> resultList = typedQuery.getResultList();

        return resultList.isEmpty() ? null : resultList.getFirst();
    }

    @Override
    public boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ExpertService> root = query.from(ExpertService.class);

        Predicate expertPredicate = cb.equal(root.get("expert").get("id"), expertId);
        Predicate servicePredicate = cb.equal(root.get("service").get("id"), serviceId);
        query.select(cb.count(root)).where(cb.and(expertPredicate, servicePredicate));

        Long count = em.createQuery(query).getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<ExpertService> findByExpertId(Integer expertId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExpertService> query = cb.createQuery(ExpertService.class);
        Root<ExpertService> root = query.from(ExpertService.class);
        query.select(root).where(cb.equal(root.get("expert").get("id"), expertId));
        return em.createQuery(query).getResultList();
    }


}
