package ir.maktabsharif.home_service.repository.customer;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.user.Customer;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryImpl extends CrudRepositoryImpl<Customer> implements CustomerRepository {
    public CustomerRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Customer> getEntityClass() {
        return Customer.class;
    }
}