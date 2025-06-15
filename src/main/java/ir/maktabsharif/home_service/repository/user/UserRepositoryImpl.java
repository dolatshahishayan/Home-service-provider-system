package ir.maktabsharif.home_service.repository.user;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.User;
import jakarta.persistence.EntityManager;

public class UserRepositoryImpl extends CrudRepositoryImpl<User> implements UserRepository {
    public UserRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }
}
