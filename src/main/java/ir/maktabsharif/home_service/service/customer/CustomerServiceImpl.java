package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.repository.customer.CustomerRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.EmailUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl extends BaseServiceImpl<Customer, Integer, CustomerRepository, CustomerMapper> implements CustomerService {
    protected final UserService userService;
    protected final WalletService walletService;
    protected final EmailUtil emailUtil;
    protected final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository repository, CustomerMapper customerMapper, UserService userService, WalletService walletService, EmailUtil emailUtil, PasswordEncoder passwordEncoder) {
        super(repository, customerMapper);
        this.userService = userService;
        this.walletService = walletService;
        this.emailUtil = emailUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Customer updateWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest) {
        if (userService.existsByEmailAndIdNot(customerSaveUpdateRequest.getEmail(), customerSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        Customer byId = findById(customerSaveUpdateRequest.getId());
        mapper.updateEntityWithDTO(customerSaveUpdateRequest, byId);
        if (customerSaveUpdateRequest.getPassword() != null) {
            byId.setPassword(passwordEncoder.encode(customerSaveUpdateRequest.getPassword()));
        }
        byId.setEmail(customerSaveUpdateRequest.getEmail().toLowerCase());
        return save(byId);
    }

    @Override
    public Customer findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(NoUserFoundWithGivenCredentialsException::new);
    }

    @Override
    public Page<Customer> findAll(Specification<Customer> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }


    @Override
    public Customer register(CustomerSaveUpdateRequest customerSaveUpdateRequest) {
        Customer customer;
        Optional<Customer> byEmail = repository.findByEmail(customerSaveUpdateRequest.getEmail());
        customer = getCustomerAndCheckVerified(customerSaveUpdateRequest, byEmail);
        getCustomer(customerSaveUpdateRequest, customer);
        save(customer);
        sendVerificationEmail(customer);
        createWalletForCustomer(customer);
        return customer;
    }

    private Customer getCustomerAndCheckVerified(CustomerSaveUpdateRequest customerSaveUpdateRequest, Optional<Customer> byEmail) {
        Customer customer;
        if (byEmail .isPresent()) {
            customer = byEmail.get();
            if (customer.getIsEmailVerified()) {
                throw new UserWithSameEmailExistsException();
            }
        } else {
            customer = mapper.mapToEntity(customerSaveUpdateRequest);
        }
        return customer;
    }

    private void getCustomer(CustomerSaveUpdateRequest customerSaveUpdateRequest, Customer customer) {
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setPassword(passwordEncoder.encode(customerSaveUpdateRequest.getPassword()));
        customer.setEmail(customerSaveUpdateRequest.getEmail().toLowerCase());
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setIsEmailVerified(false);
    }

    private void createWalletForCustomer(Customer customer) {
        Customer byEmail = findByEmail(customer.getEmail());
        WalletSaveUpdateRequest walletSaveUpdateRequest = new WalletSaveUpdateRequest();
        walletSaveUpdateRequest.setUserId(byEmail.getId());
        walletService.saveWithDTO(walletSaveUpdateRequest);
    }

    private void sendVerificationEmail(Customer customer) {
        EmailVerificationToken token = emailUtil.createToken(customer, 60);
        String link = emailUtil.buildFrontendVerificationLink(token);
        emailUtil.sendVerificationEmail(customer.getEmail(), customer.getFirstName(), link);
    }

}
