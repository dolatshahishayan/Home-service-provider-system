package ir.maktabsharif.home_service.repository.user;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends CrudRepositoryImpl<User> implements UserRepository {
    public UserRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public boolean existsByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        query.select(cb.count(root))
                .where(cb.equal(root.get("email"), email));

        Long count = em.createQuery(query).getSingleResult();
        return count > 0;
    }

}
