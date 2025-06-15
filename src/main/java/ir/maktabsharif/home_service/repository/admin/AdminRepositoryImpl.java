package ir.maktabsharif.home_service.repository.admin;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.Admin;
import jakarta.persistence.EntityManager;

public class AdminRepositoryImpl extends CrudRepositoryImpl<Admin> implements AdminRepository {
    public AdminRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Admin> getEntityClass() {
        return Admin.class;
    }
}
