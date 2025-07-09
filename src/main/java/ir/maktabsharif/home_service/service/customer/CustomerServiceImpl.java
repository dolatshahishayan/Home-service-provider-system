package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.repository.customer.CustomerRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer, CustomerRepository, CustomerMapper> implements CustomerService {
    protected final UserService userService;
    protected final WalletService walletService;

    public CustomerServiceImpl(CustomerRepository repository, CustomerMapper customerMapper, UserService userService, WalletService walletService) {
        super(repository, customerMapper);
        this.userService = userService;
        this.walletService = walletService;
    }

    @Override
    public Customer updateWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest) {
        if (userService.existsByEmailAndIdNot(customerSaveUpdateRequest.getEmail(), customerSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        Customer byId = findById(customerSaveUpdateRequest.getId());
        mapper.updateEntityWithDTO(customerSaveUpdateRequest, byId);
        byId.setEmail(customerSaveUpdateRequest.getEmail().toLowerCase());
        return save(byId);
    }

    @Override
    public Customer findByEmail(String email) {
       return repository.findByEmail(email).orElseThrow(NoUserFoundWithGivenCredentialsException::new);
    }

    @Override
    public List<Customer> findAll(Specification<Customer> spec) {
        return repository.findAll(spec);
    }

    @Override
    public Customer register(CustomerSaveUpdateRequest customerSaveUpdateRequest) {
        if (userService.existsByEmail(customerSaveUpdateRequest.getEmail())) {
            throw new UserWithSameEmailExistsException();
        }
        Customer customer = mapper.mapToEntity(customerSaveUpdateRequest);
        customer.setRole(Role.CUSTOMER);
        customer.setEmail(customerSaveUpdateRequest.getEmail().toLowerCase());
        customer.setRegistrationDate(LocalDateTime.now());
        save(customer);
        Customer byEmail = findByEmail(customer.getEmail());
        WalletSaveUpdateRequest walletSaveUpdateRequest = new WalletSaveUpdateRequest();
        walletSaveUpdateRequest.setUserId(byEmail.getId());
        walletService.saveWithDTO(walletSaveUpdateRequest);
        return customer;
    }

}
