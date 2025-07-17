package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.dto.user.LoginDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.mapper.user.UserMapper;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserMapper mapper;

    @Mock
    ExpertServiceService expertServiceService;

    @Mock
    ExpertService expertService;

    @Mock
    CustomerService customerService;

    @Mock
    ir.maktabsharif.home_service.repository.user.UserRepository repository;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        String email = "test@example.com";
        when(repository.existsByEmail(email)).thenReturn(true);
        assertTrue(userService.existsByEmail(email));
        verify(repository).existsByEmail(email);
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailExistsForOtherUser() {
        String email = "test@example.com";
        Integer id = 5;
        when(repository.existsByEmailAndIdNot(email, id)).thenReturn(false);
        assertFalse(userService.existsByEmailAndIdNot(email, id));
        verify(repository).existsByEmailAndIdNot(email, id);
    }

    @Test
    void findByEmailAndPassword_ShouldReturnUser_WhenCredentialsCorrect() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("pass");

        User user = new User();
        when(repository.findByEmailAndPassword(loginDTO.getEmail().toLowerCase(), loginDTO.getPassword()))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmailAndPassword(loginDTO);
        assertSame(user, result);
        verify(repository).findByEmailAndPassword(loginDTO.getEmail().toLowerCase(), loginDTO.getPassword());
    }

    @Test
    void findByEmailAndPassword_ShouldThrow_WhenUserNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("wrongpass");

        when(repository.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NoUserFoundWithGivenCredentialsException.class, () -> userService.findByEmailAndPassword(loginDTO));
        verify(repository).findByEmailAndPassword(loginDTO.getEmail().toLowerCase(), loginDTO.getPassword());
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchUsers_ShouldReturnExperts_WhenRoleIsExpert() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setRole(Role.EXPERT);
        request.setName("John");
        request.setServiceIds(List.of(1, 2));
        request.setMinScore(3.0);
        request.setMaxScore(5.0);

        List<Integer> expertIds = List.of(10, 20);
        Expert expert1 = new Expert();
        Expert expert2 = new Expert();
        when(expertServiceService.findExpertIdsByServiceIds(request.getServiceIds())).thenReturn(expertIds);

        when(expertService.findAll(any(Specification.class))).thenReturn(List.of(expert1, expert2));
        UserSearchResponseDTO dto1 = new UserSearchResponseDTO();
        UserSearchResponseDTO dto2 = new UserSearchResponseDTO();
        when(mapper.mapExpertToSearchResponse(expert1)).thenReturn(dto1);
        when(mapper.mapExpertToSearchResponse(expert2)).thenReturn(dto2);

        List<UserSearchResponseDTO> results = userService.searchUsers(request);

        assertEquals(2, results.size());
        assertTrue(results.contains(dto1));
        assertTrue(results.contains(dto2));

        verify(expertServiceService).findExpertIdsByServiceIds(request.getServiceIds());
        verify(expertService).findAll(any(Specification.class));
        verify(mapper).mapExpertToSearchResponse(expert1);
        verify(mapper).mapExpertToSearchResponse(expert2);
        verifyNoInteractions(customerService);
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchUsers_ShouldReturnCustomers_WhenRoleIsCustomer() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setRole(Role.ROLE_CUSTOMER);
        request.setName("Alice");

        Customer customer1 = new Customer();
        Customer customer2 = new Customer();

        when(customerService.findAll(any(Specification.class))).thenReturn(List.of(customer1, customer2));
        UserSearchResponseDTO dto1 = new UserSearchResponseDTO();
        UserSearchResponseDTO dto2 = new UserSearchResponseDTO();
        when(mapper.mapCustomerToSearchResponse(customer1)).thenReturn(dto1);
        when(mapper.mapCustomerToSearchResponse(customer2)).thenReturn(dto2);

        List<UserSearchResponseDTO> results = userService.searchUsers(request);

        assertEquals(2, results.size());
        assertTrue(results.contains(dto1));
        assertTrue(results.contains(dto2));

        verify(customerService).findAll(any(Specification.class));
        verify(mapper).mapCustomerToSearchResponse(customer1);
        verify(mapper).mapCustomerToSearchResponse(customer2);
        verifyNoInteractions(expertService);
        verifyNoInteractions(expertServiceService);
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchUsers_ShouldReturnExperts_WhenRoleIsNullButHasExpertFilters() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setRole(null);
        request.setServiceIds(List.of(5));
        request.setMinScore(1.0);

        List<Integer> expertIds = List.of(99);
        Expert expert = new Expert();

        when(expertServiceService.findExpertIdsByServiceIds(request.getServiceIds())).thenReturn(expertIds);
        when(expertService.findAll(any(Specification.class))).thenReturn(List.of(expert));
        UserSearchResponseDTO dto = new UserSearchResponseDTO();
        when(mapper.mapExpertToSearchResponse(expert)).thenReturn(dto);

        List<UserSearchResponseDTO> results = userService.searchUsers(request);

        assertEquals(1, results.size());
        assertTrue(results.contains(dto));

        verify(expertServiceService).findExpertIdsByServiceIds(request.getServiceIds());
        verify(expertService).findAll(any(Specification.class));
        verify(mapper).mapExpertToSearchResponse(expert);
        verifyNoInteractions(customerService);
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchUsers_ShouldReturnCustomers_WhenRoleIsNullAndNoExpertFilters() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setRole(null);
        request.setServiceIds(null); // no expert filters
        request.setName("CustomerName");

        Customer customer = new Customer();
        when(customerService.findAll(any(Specification.class))).thenReturn(List.of(customer));
        UserSearchResponseDTO dto = new UserSearchResponseDTO();
        when(mapper.mapCustomerToSearchResponse(customer)).thenReturn(dto);

        List<UserSearchResponseDTO> results = userService.searchUsers(request);

        assertEquals(1, results.size());
        assertTrue(results.contains(dto));

        verify(customerService).findAll(any(Specification.class));
        verify(mapper).mapCustomerToSearchResponse(customer);
        verifyNoInteractions(expertService);
        verifyNoInteractions(expertServiceService);
    }
}
