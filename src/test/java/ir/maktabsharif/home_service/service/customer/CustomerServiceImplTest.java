package ir.maktabsharif.home_service.service.customer;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock private UserService userService;
    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerMapper customerMapper;
    @Mock private WalletService walletService;

    @InjectMocks private CustomerServiceImpl customerService;

    @Test
    void updateWithDTO_shouldThrow_whenEmailIsUsedByOther() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("duplicate@example.com");

        when(userService.existsByEmailAndIdNot("duplicate@example.com", 1)).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> customerService.updateWithDTO(dto));
    }

    @Test
    void updateWithDTO_shouldUpdate_whenEmailIsUnique() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("unique@example.com");

        Customer customer = new Customer();
        customer.setId(1);

        when(userService.existsByEmailAndIdNot("unique@example.com", 1)).thenReturn(false);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        doAnswer(invocation -> {
            Customer target = invocation.getArgument(1);
            target.setFirstName("Ali");
            target.setLastName("Test");
            return null;
        }).when(customerMapper).updateEntityWithDTO(any(), any());

        customerService.updateWithDTO(dto);

        assertEquals("unique@example.com", customer.getEmail());
        verify(customerRepository).save(customer);
    }

    @Test
    void register_shouldThrow_whenEmailExists() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setEmail("exists@example.com");

        when(userService.existsByEmail("exists@example.com")).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> customerService.register(dto));
    }

    @Test
    void register_shouldSave_whenEmailIsUnique() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setEmail("new@example.com");

        Customer customer = new Customer();
        customer.setEmail("new@example.com");
        customer.setId(5);

        when(userService.existsByEmail("new@example.com")).thenReturn(false);
        when(customerMapper.mapToEntity(dto)).thenReturn(customer);
        when(customerRepository.findByEmail("new@example.com")).thenReturn(Optional.of(customer));

        Customer result = customerService.register(dto);

        assertNotNull(result.getRegistrationDate());
        assertEquals(Role.ROLE_CUSTOMER, result.getRole());
        verify(customerRepository).save(customer);
        verify(walletService).saveWithDTO(any(WalletSaveUpdateRequest.class));
    }

    @Test
    void findByEmail_shouldReturnCustomer_whenEmailExists() {
        Customer customer = new Customer();
        when(customerRepository.findByEmail("found@example.com")).thenReturn(Optional.of(customer));

        Customer result = customerService.findByEmail("found@example.com");
        assertSame(customer, result);
    }

    @Test
    void findByEmail_shouldThrow_whenNotFound() {
        when(customerRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(NoUserFoundWithGivenCredentialsException.class, () -> customerService.findByEmail("notfound@example.com"));
    }

    @Test
    void findAll_shouldReturnListFromRepository() {
        Specification<Customer> spec = (root, query, cb) -> null;
        when(customerRepository.findAll(spec)).thenReturn(Collections.emptyList());

        assertTrue(customerService.findAll(spec).isEmpty());
    }
}
