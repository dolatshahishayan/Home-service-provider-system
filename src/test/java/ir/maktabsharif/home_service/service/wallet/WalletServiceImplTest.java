package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository repository;

    @Mock
    private WalletMapper mapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletServiceImpl service;


    @Test
    void saveWithDTO_shouldMapAndSave() {
        WalletSaveUpdateRequest dto = new WalletSaveUpdateRequest();
        Wallet wallet = new Wallet();
        dto.setUserId(1);
        when(mapper.mapToEntity(dto)).thenReturn(wallet);
        when(userService.findById(anyInt())).thenReturn(new User());

        service.saveWithDTO(dto);

        verify(repository).beginTransaction();
        verify(repository).save(wallet);
        verify(repository).commitTransaction();
    }


    @Test
    void addCreditToWallet_shouldAddCreditAndUpdate() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100.0);
        Integer userId = 1;
        Double creditToAdd = 50.0;

        when(repository.findByUserId(userId)).thenReturn(wallet);

        service.addCreditToWallet(creditToAdd, userId);

        assertEquals(150.0, wallet.getBalance());
        verify(repository).beginTransaction();
        verify(repository).update(wallet);
        verify(repository).commitTransaction();
    }


    @Test
    void saveWithExpert_shouldCreateWalletForExpert() {
        Expert expert = new Expert();
        expert.setId(10);

        WalletSaveUpdateRequest expectedRequest = new WalletSaveUpdateRequest();
        expectedRequest.setUserId(10);
        expectedRequest.setBalance(0.0);

        Wallet mappedWallet = new Wallet();
        when(mapper.mapToEntity(any())).thenReturn(mappedWallet);
        when(userService.findById(anyInt())).thenReturn(new User());

        service.saveWithExpert(expert);

        verify(repository).beginTransaction();
        verify(repository).save(mappedWallet);
        verify(repository).commitTransaction();
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

        when(repository.findByUserId(1)).thenReturn(customerWallet);

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

        when(repository.findByUserId(1)).thenReturn(customerWallet);
        when(repository.findByUserId(2)).thenReturn(expertWallet);

        service.payFromWallet(order, suggestion);

        assertEquals(20.0, customerWallet.getBalance());
        assertEquals(100.0, expertWallet.getBalance());

        verify(repository, times(2)).beginTransaction();
        verify(repository, times(2)).update(any(Wallet.class));
        verify(repository, times(2)).commitTransaction();
    }


    @Test
    void findByUserId_shouldReturnWallet() {
        Wallet wallet = new Wallet();
        when(repository.findByUserId(5)).thenReturn(wallet);

        Wallet result = service.findByUserId(5);

        assertEquals(wallet, result);
    }
}
