package ir.maktabsharif.home_service.repository.service;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.service.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceRepositoryImpl extends CrudRepositoryImpl<Service> implements ServiceRepository {
    public ServiceRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Service> getEntityClass() {
        return Service.class;
    }

    @Override
    public boolean existsByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Service> serviceRoot = cq.from(Service.class);
        cq.select(cb.count(serviceRoot));
        cq.where(cb.equal(serviceRoot.get("name"), name));
        Long count = em.createQuery(cq).getSingleResult();
        return count > 0;
    }
}
