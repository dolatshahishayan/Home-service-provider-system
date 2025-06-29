package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Expert_ServiceRepositoryImpl extends CrudRepositoryImpl<Expert_Service> implements Expert_ServiceRepository {
    public Expert_ServiceRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Expert_Service> getEntityClass() {
        return Expert_Service.class;
    }

    @Override
    public Expert_Service findByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Expert_Service> query = cb.createQuery(Expert_Service.class);
        Root<Expert_Service> root = query.from(Expert_Service.class);

        Predicate expertPredicate = cb.equal(root.get("expert").get("id"), expertId);
        Predicate servicePredicate = cb.equal(root.get("service").get("id"), serviceId);

        query.select(root).where(cb.and(expertPredicate, servicePredicate));

        TypedQuery<Expert_Service> typedQuery = em.createQuery(query);
        List<Expert_Service> resultList = typedQuery.getResultList();

        return resultList.isEmpty() ? null : resultList.getFirst();
    }

    @Override
    public boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Expert_Service> root = query.from(Expert_Service.class);

        Predicate expertPredicate = cb.equal(root.get("expert").get("id"), expertId);
        Predicate servicePredicate = cb.equal(root.get("service").get("id"), serviceId);
        query.select(cb.count(root)).where(cb.and(expertPredicate, servicePredicate));

        Long count = em.createQuery(query).getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<Expert_Service> findByExpertId(Integer expertId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Expert_Service> query = cb.createQuery(Expert_Service.class);
        Root<Expert_Service> root = query.from(Expert_Service.class);
        query.select(root).where(cb.equal(root.get("expert").get("id"), expertId));
        return em.createQuery(query).getResultList();
    }


}
