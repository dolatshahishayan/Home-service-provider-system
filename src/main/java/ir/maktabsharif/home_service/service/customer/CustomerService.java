package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Customer;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService extends BaseService<Customer,Integer> {
    Customer register(CustomerSaveUpdateRequest customerSaveUpdateRequest);
    Customer updateWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest);
    Customer findByEmail(String email);
    List<Customer> findAll(Specification<Customer> spec);
}
