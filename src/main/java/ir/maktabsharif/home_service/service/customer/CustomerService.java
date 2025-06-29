package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Admin;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;

public interface CustomerService extends BaseService<Customer, CustomerSaveUpdateRequest> {
    void register(CustomerSaveUpdateRequest customerSaveUpdateRequest);
    void updateWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest);
}
