package ir.maktabsharif.home_service.repository.customer;

import ir.maktabsharif.home_service.model.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Integer>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
}
