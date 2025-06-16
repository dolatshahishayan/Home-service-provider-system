package ir.maktabsharif.home_service.repository.customer;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepositoryImpl extends CrudRepositoryImpl<Customer> implements CustomerRepository {
    public CustomerRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Customer> getEntityClass() {
        return Customer.class;
    }

    @Override
    public Customer findByEmailAndPassword(String email,String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);
        query.select(root).where(cb.and(cb.equal(root.get("email"), email)),cb.equal(root.get("password"), password));
        TypedQuery<Customer> typedQuery = em.createQuery(query);
        List<Customer> resultList = typedQuery.getResultList();
        return resultList.isEmpty() ? null : resultList.getFirst();
    }

}
