package ir.maktabsharif.home_service.repository.expert_subservice;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.expert_subservice.Expert_SubService;
import jakarta.persistence.EntityManager;

public class Expert_SubServiceRepositoryImpl extends CrudRepositoryImpl<Expert_SubService> implements Expert_SubServiceRepository {
    public Expert_SubServiceRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Expert_SubService> getEntityClass() {
        return Expert_SubService.class;
    }
}
