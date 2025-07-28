package ir.maktabsharif.home_service.service.customer;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testFindByEmail_found() {
        Customer customer = new Customer();
        when(customerRepository.findByEmail("a@test.com")).thenReturn(Optional.of(customer));

        Customer result = customerService.findByEmail("a@test.com");

        assertEquals(customer, result);
    }

    @Test
    void testFindByEmail_notFound() {
        when(customerRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(NoUserFoundWithGivenCredentialsException.class,
                () -> customerService.findByEmail("notfound@test.com"));
    }

    @Test
    void testUpdateWithDTO_success() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("new@email.com");
        dto.setPassword("newPass");

        Customer customer = new Customer();
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(userService.existsByEmailAndIdNot("new@email.com", 1)).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("hashed");

        Customer updated = new Customer();
        when(customerRepository.save(any())).thenReturn(updated);

        Customer result = customerService.updateWithDTO(dto);

        assertEquals(updated, result);
        verify(customerMapper).updateEntityWithDTO(dto, customer);
        assertEquals("hashed", customer.getPassword());
        assertEquals("new@email.com", customer.getEmail());
    }

    @Test
    void testUpdateWithDTO_duplicateEmail() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("exists@email.com");

        when(userService.existsByEmailAndIdNot("exists@email.com", 1)).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> customerService.updateWithDTO(dto));
    }

    @Test
    void testRegister_newCustomer() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setEmail("customer@test.com");
        dto.setPassword("1234");

        when(customerRepository.findByEmail("customer@test.com")).thenReturn(Optional.empty());

        Customer customer = new Customer();
        when(passwordEncoder.encode("1234")).thenReturn("hashed");

        EmailVerificationToken token = new EmailVerificationToken();
        when(emailUtil.createToken(any(), eq(60))).thenReturn(token);
        when(emailUtil.buildFrontendVerificationLink(token)).thenReturn("https://link");

        when(customerRepository.save(any())).thenReturn(customer);
        when(customerRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(customer));

        customer.setIsEmailVerified(false);
        Customer result = customerService.register(dto);

        assertEquals(customer, result);
        assertEquals("customer@test.com", customer.getEmail());
        assertEquals("hashed", customer.getPassword());
        assertEquals(Role.ROLE_CUSTOMER, customer.getRole());
        assertFalse(customer.getIsEmailVerified());

        verify(emailUtil).sendVerificationEmail(eq("customer@test.com"), any(), eq("https://link"));
        verify(walletService).saveWithDTO(any(WalletSaveUpdateRequest.class));
    }

    @Test
    void testRegister_existingVerifiedCustomer_throwsException() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setEmail("verified@test.com");

        Customer customer = new Customer();
        customer.setIsEmailVerified(true);
        when(customerRepository.findByEmail("verified@test.com")).thenReturn(Optional.of(customer));

        assertThrows(UserWithSameEmailExistsException.class, () -> customerService.register(dto));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAll_withSpec() {
        Specification<Customer> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Customer> page = mock(Page.class);

        when(customerRepository.findAll(spec, pageable)).thenReturn(page);

        Page<Customer> result = customerService.findAll(spec, pageable);

        assertEquals(page, result);
    }
}
