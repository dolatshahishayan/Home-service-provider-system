package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletServiceImpl service;

    @Test
    void testSaveWithDTO() {
        Integer userId = 1;
        User mockUser = new User();
        mockUser.setId(userId);

        WalletSaveUpdateRequest request = new WalletSaveUpdateRequest();
        request.setUserId(userId);

        Wallet savedWallet = new Wallet();
        savedWallet.setUser(mockUser);
        savedWallet.setBalance(0.0);

        when(userService.findById(userId)).thenReturn(mockUser);
        when(repository.save(any(Wallet.class))).thenReturn(savedWallet);

        Wallet result = service.saveWithDTO(request);

        assertNotNull(result);
        assertEquals(mockUser, result.getUser());
        assertEquals(0.0, result.getBalance());

        verify(userService).findById(userId);
        verify(repository).save(any(Wallet.class));
    }


    @Test
    void addCreditToWallet_shouldAddCreditAndUpdate() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);
        Integer userId = 1;
        Double creditToAdd = 50.0;

        when(repository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        service.addCreditToWallet(creditToAdd, userId);

        assertEquals(150.0, wallet.getBalance());
        verify(repository).save(wallet);
    }

    @Test
    void payFromWallet_shouldThrow_whenInsufficientBalance() {
        Order order = new Order();
        Customer customer = new Customer();
        customer.setId(1);
        order.setCustomer(customer);

        Expert expert = new Expert();
        expert.setId(2);
        order.setExpert(expert);

        Suggestion suggestion = new Suggestion();
        suggestion.setPrice(200.0);

        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(100.0);

        when(repository.findByUserId(1)).thenReturn(Optional.of(customerWallet));

        assertThrows(CouldNotUpdateException.class, () ->
                service.payFromWallet(order, suggestion));
    }

    @Test
    void payFromWallet_shouldTransferMoney_whenSufficientBalance() {
        Order order = new Order();
        Customer customer = new Customer();
        customer.setId(1);
        order.setCustomer(customer);

        Expert expert = new Expert();
        expert.setId(2);
        order.setExpert(expert);

        Suggestion suggestion = new Suggestion();
        suggestion.setPrice(80.0);

        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(100.0);

        Wallet expertWallet = new Wallet();
        expertWallet.setBalance(20.0);

        when(repository.findByUserId(1)).thenReturn(Optional.of(customerWallet));
        when(repository.findByUserId(2)).thenReturn(Optional.of(expertWallet));

        service.payFromWallet(order, suggestion);

        assertEquals(20.0, customerWallet.getBalance());
        assertEquals(100.0, expertWallet.getBalance());

        verify(repository, times(2)).save(any(Wallet.class));
    }


    @Test
    void findByUserId_shouldReturnWallet() {
        Wallet wallet = new Wallet();
        when(repository.findByUserId(5)).thenReturn(Optional.of(wallet));

        Wallet result = service.findByUserId(5);

        assertEquals(wallet, result);
    }
}
