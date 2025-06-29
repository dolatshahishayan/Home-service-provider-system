package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.exception.NoUserLoggedInException;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.repository.user.UserRepository;
import ir.maktabsharif.home_service.util.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;


    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        when(repository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(service.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        when(repository.existsByEmail("none@example.com")).thenReturn(false);
        assertFalse(service.existsByEmail("none@example.com"));
    }


    @Test
    void existsByEmailAndIdNot_shouldReturnTrue_whenAnotherUserWithEmailExists() {
        when(repository.existsByEmailAndIdNot("duplicate@example.com", 1)).thenReturn(true);
        assertTrue(service.existsByEmailAndIdNot("duplicate@example.com", 1));
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnFalse_whenNoOtherUserHasEmail() {
        when(repository.existsByEmailAndIdNot("unique@example.com", 2)).thenReturn(false);
        assertFalse(service.existsByEmailAndIdNot("unique@example.com", 2));
    }


    @Test
    void login_shouldReturnUser_whenCredentialsAreValid() {
        User user = new User();
        user.setId(10);
        user.setEmail("user@example.com");

        when(repository.findByEmailAndPassword("user@example.com", "1234")).thenReturn(user);

        User result = service.login("user@example.com", "1234");

        assertEquals(user, result);
        assertNotNull(Session.getCurrentUser());
        assertEquals("user@example.com", Session.getCurrentUser().getEmail());
        assertEquals(10, Session.getCurrentUser().getUserId());
    }

    @Test
    void login_shouldThrow_whenCredentialsInvalid() {
        when(repository.findByEmailAndPassword("bad@example.com", "wrong")).thenReturn(null);

        assertThrows(NoUserFoundWithGivenCredentialsException.class, () ->
                service.login("bad@example.com", "wrong"));
    }


    @Test
    void logout_shouldClearSession_whenUserLoggedIn() {
        Session.setCurrentUser(new UserSessionDTO(1, "user@example.com"));

        service.logout();

        assertNull(Session.getCurrentUser());
    }

    @Test
    void logout_shouldThrow_whenNoUserLoggedIn() {
        Session.setCurrentUser(null);

        assertThrows(NoUserLoggedInException.class, () -> service.logout());
    }
}
