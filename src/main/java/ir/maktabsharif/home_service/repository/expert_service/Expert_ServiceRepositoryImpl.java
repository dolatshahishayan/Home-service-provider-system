package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;
import jakarta.persistence.EntityManager;

public class Expert_ServiceRepositoryImpl extends CrudRepositoryImpl<Expert_Service> implements Expert_ServiceRepository {
    public Expert_ServiceRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Expert_Service> getEntityClass() {
        return Expert_Service.class;
    }
}
