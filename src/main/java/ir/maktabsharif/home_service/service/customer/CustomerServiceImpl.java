package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.repository.customer.CustomerRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerSaveUpdateRequest, CustomerRepository, CustomerMapper> implements CustomerService {
    protected final UserService userService;
    public CustomerServiceImpl(CustomerRepository repository, CustomerMapper mapper, UserService userService) {
        super(repository, mapper);
        this.userService = userService;
    }

    public void updateWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest) {
        if (userService.existsByEmail(customerSaveUpdateRequest.getEmail())) {
            throw new UserWithSameEmailExistsException();
        }
        update(findById(customerSaveUpdateRequest.getId()));
    }

    @Override
    public void register(CustomerSaveUpdateRequest customerSaveUpdateRequest) {
        if (userService.existsByEmail(customerSaveUpdateRequest.getEmail())){
            throw new UserWithSameEmailExistsException();
        }
        Customer customer=mapper.mapToEntity(customerSaveUpdateRequest);
        customer.setRegistrationDate(LocalDateTime.now());
        save(customer);
    }

}
