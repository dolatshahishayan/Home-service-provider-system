package ir.maktabsharif.home_service.repository.service;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.service.Service;
import jakarta.persistence.EntityManager;
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
}
