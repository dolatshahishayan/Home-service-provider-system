package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
