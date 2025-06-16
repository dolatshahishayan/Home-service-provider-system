package ir.maktabsharif.home_service.repository.admin;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.Admin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRepositoryImpl extends CrudRepositoryImpl<Admin> implements AdminRepository {
    public AdminRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Admin> getEntityClass() {
        return Admin.class;
    }

    @Override
    public Admin findByEmailAndPassword(String email,String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Admin> query = cb.createQuery(Admin.class);
        Root<Admin> root = query.from(Admin.class);
        query.select(root).where(cb.and(cb.equal(root.get("email"), email)),cb.equal(root.get("password"), password));
        TypedQuery<Admin> typedQuery = em.createQuery(query);
        List<Admin> resultList = typedQuery.getResultList();
        return resultList.isEmpty() ? null : resultList.getFirst();
    }

}
