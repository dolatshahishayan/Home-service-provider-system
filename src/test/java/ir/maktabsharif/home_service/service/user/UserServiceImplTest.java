package ir.maktabsharif.home_service.service.user;

import ir.maktabsharif.home_service.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

}
