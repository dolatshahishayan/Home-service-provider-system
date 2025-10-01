package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ExpertServiceCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Integer> findExpertIdsByServiceIds(List<Integer> serviceIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);

        Root<ExpertService> root = cq.from(ExpertService.class);

        cq.select(root.get("expert").get("id")).distinct(true);

        if (serviceIds != null && !serviceIds.isEmpty()) {
            Predicate servicePredicate = root.get("service").get("id").in(serviceIds);
            cq.where(servicePredicate);
        }

        return entityManager.createQuery(cq).getResultList();
    }
}
