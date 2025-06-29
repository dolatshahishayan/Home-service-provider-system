package ir.maktabsharif.home_service.repository.user;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public boolean existsByEmailAndIdNot(String email, Integer id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Predicate emailPredicate = cb.equal(root.get("email"), email);
        Predicate idNotPredicate = cb.notEqual(root.get("id"), id);
        Predicate finalPredicate = cb.and(emailPredicate, idNotPredicate);

        query.select(cb.count(root)).where(finalPredicate);

        Long count = em.createQuery(query).getSingleResult();
        return count > 0;
    }
    @Override
    public User findByEmailAndPassword(String email, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(cb.and(cb.equal(root.get("email"), email)),cb.equal(root.get("password"), password));
        TypedQuery<User> typedQuery = em.createQuery(query);
        List<User> resultList = typedQuery.getResultList();
        return resultList.isEmpty() ? null : resultList.getFirst();
    }

}
