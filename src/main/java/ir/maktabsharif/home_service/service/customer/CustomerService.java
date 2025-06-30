package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Customer;

public interface CustomerService extends BaseService<Customer> {
    void register(CustomerSaveUpdateRequest customerSaveUpdateRequest);
    void updateWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest);
}
