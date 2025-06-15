package ir.maktabsharif.home_service.repository.subservice;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.subservice.SubService;
import jakarta.persistence.EntityManager;

public class SubServiceRepositoryImpl extends CrudRepositoryImpl<SubService> implements SubServiceRepository {
    public SubServiceRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<SubService> getEntityClass() {
        return SubService.class;
    }
}
