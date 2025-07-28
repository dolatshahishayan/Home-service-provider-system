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
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import ir.maktabsharif.home_service.service.customer.CustomerService;
import ir.maktabsharif.home_service.service.expert.ExpertService;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    ExpertServiceService expertServiceService;

    @Mock
    ExpertService expertService;

    @Mock
    CustomerService customerService;

    @Test
    void existsByEmail_callsRepository() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void existsByEmailAndIdNot_callsRepository() {
        String email = "test@example.com";
        Integer id = 5;
        when(userRepository.existsByEmailAndIdNot(email, id)).thenReturn(true);

        boolean result = userService.existsByEmailAndIdNot(email, id);

        assertTrue(result);
        verify(userRepository).existsByEmailAndIdNot(email, id);
    }

    @Test
    void findByEmailAndPassword_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("pass");

        User user = new User();

        when(userRepository.findByEmailAndPassword(loginDTO.getEmail().toLowerCase(), loginDTO.getPassword()))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmailAndPassword(loginDTO);

        assertEquals(user, result);
    }

    @Test
    void findByEmailAndPassword_noUser_throws() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("wrongPass");

        when(userRepository.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NoUserFoundWithGivenCredentialsException.class, () -> userService.findByEmailAndPassword(loginDTO));
    }

    @Test
    void findByEmail_success() {
        String email = "user@example.com";
        User user = new User();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void findByEmail_noUser_throws() {
        String email = "user@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NoUserFoundWithGivenCredentialsException.class, () -> userService.findByEmail(email));
    }

    @Test
    void loadUserByUsername_success() {
        String username = "user@example.com";
        User user = new User();

        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(user, userDetails.user());
    }

    @Test
    void loadUserByUsername_noUser_throws() {
        String username = "user@example.com";

        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(NoUserFoundWithGivenCredentialsException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void searchUsers_shouldReturnCustomersOnly_whenRoleIsCustomer() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setRole(Role.ROLE_CUSTOMER);
        request.setName("Ali");

        Customer customer = new Customer(); customer.setId(1);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        UserSearchResponseDTO responseDTO = new UserSearchResponseDTO();
        when(customerService.findAll(any(), any())).thenReturn(customerPage);
        when(userMapper.mapCustomerToSearchResponse(customer)).thenReturn(responseDTO);

        Page<UserSearchResponseDTO> result = userService.searchUsers(request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(customerService).findAll(any(), any());
        verify(userMapper).mapCustomerToSearchResponse(customer);
    }

    @Test
    void searchUsers_shouldReturnExpertsOnly_whenRoleIsExpert() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setRole(Role.ROLE_EXPERT);
        request.setName("Ali");

        Expert expert = new Expert(); expert.setId(2);
        Page<Expert> expertPage = new PageImpl<>(List.of(expert));

        UserSearchResponseDTO responseDTO = new UserSearchResponseDTO();
        when(expertServiceService.findExpertIdsByServiceIds(null)).thenReturn(List.of());
        when(expertService.findAll(any(), any())).thenReturn(expertPage);
        when(userMapper.mapExpertToSearchResponse(expert)).thenReturn(responseDTO);

        Page<UserSearchResponseDTO> result = userService.searchUsers(request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(expertService).findAll(any(), any());
        verify(userMapper).mapExpertToSearchResponse(expert);
    }

    @Test
    void searchUsers_shouldReturnExpertsOnly_whenNoRoleAndHasExpertFilters() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setMinScore(2.0);
        request.setMaxScore(5.0);
        request.setServiceIds(List.of(1, 2));

        Expert expert = new Expert(); expert.setId(3);
        Page<Expert> expertPage = new PageImpl<>(List.of(expert));

        when(expertServiceService.findExpertIdsByServiceIds(request.getServiceIds())).thenReturn(List.of(3));
        when(expertService.findAll(any(), any())).thenReturn(expertPage);
        when(userMapper.mapExpertToSearchResponse(expert)).thenReturn(new UserSearchResponseDTO());

        Page<UserSearchResponseDTO> result = userService.searchUsers(request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(expertService).findAll(any(), any());
    }

    @Test
    void searchUsers_shouldReturnCustomersOnly_whenNoRoleAndNoExpertFilters() {
        UserSearchRequestDTO request = new UserSearchRequestDTO();
        request.setName("Sara");

        Customer customer = new Customer(); customer.setId(4);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerService.findAll(any(), any())).thenReturn(customerPage);
        when(userMapper.mapCustomerToSearchResponse(customer)).thenReturn(new UserSearchResponseDTO());

        Page<UserSearchResponseDTO> result = userService.searchUsers(request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(customerService).findAll(any(), any());
    }

}
