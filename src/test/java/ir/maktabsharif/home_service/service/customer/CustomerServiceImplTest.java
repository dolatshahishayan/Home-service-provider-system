package ir.maktabsharif.home_service.service.customer;

import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.customer.CustomerMapper;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.repository.customer.CustomerRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void updateWithDTO_ShouldThrowException_WhenEmailUsedByAnotherUser() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("exists@example.com");

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> customerService.updateWithDTO(dto));
    }

    @Test
    void updateWithDTO_ShouldUpdateCustomer_WhenEmailIsUnique() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setId(1);
        dto.setFirstName("Shayan");
        dto.setLastName("Dolatshahi");
        dto.setEmail("unique@example.com");
        dto.setPassword("1234");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(false);
        when(customerRepository.findById(dto.getId())).thenReturn(Optional.of(existingCustomer));

        customerService.updateWithDTO(dto);

        assertEquals("Shayan", existingCustomer.getFirstName());
        assertEquals("Dolatshahi", existingCustomer.getLastName());
        assertEquals("unique@example.com", existingCustomer.getEmail());
        assertEquals("1234", existingCustomer.getPassword());

        verify(customerRepository).beginTransaction();
        verify(customerRepository).update(existingCustomer);
        verify(customerRepository).commitTransaction();
    }
    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setEmail("exists@example.com");

        when(userService.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () -> customerService.register(dto));
    }

    @Test
    void register_ShouldSaveCustomer_WhenEmailIsUnique() {
        CustomerSaveUpdateRequest dto = new CustomerSaveUpdateRequest();
        dto.setEmail("unique@example.com");

        Customer customer = new Customer();

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(customer);

        customerService.register(dto);

        assertNotNull(customer.getRegistrationDate());

        verify(customerRepository).beginTransaction();
        verify(customerRepository).save(customer);
        verify(customerRepository).commitTransaction();
    }

}
