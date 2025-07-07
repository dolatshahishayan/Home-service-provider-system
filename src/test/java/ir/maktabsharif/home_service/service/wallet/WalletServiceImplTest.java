package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock private WalletRepository repository;
    @Mock private UserService userService;
    @Mock private OrderService orderService;
    @Mock private SuggestionService suggestionService;
    @Mock private TransactionService transactionService;

    @InjectMocks
    private WalletServiceImpl service;

    private Wallet wallet;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(1000.0);
    }

    @Test
    void saveWithDTO_shouldCreateWalletWithZeroBalance() {
        WalletSaveUpdateRequest dto = new WalletSaveUpdateRequest();
        dto.setUserId(1);

        when(userService.findById(1)).thenReturn(user);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Wallet saved = service.saveWithDTO(dto);

        assertEquals(0.0, saved.getBalance());
        assertEquals(user, saved.getUser());
        verify(repository).save(any(Wallet.class));
    }

    @Test
    void addCreditToWallet_shouldAddBalanceAndCreateTransaction() {
        when(repository.findByUserId(1)).thenReturn(Optional.of(wallet));
        when(userService.findById(1)).thenReturn(user);

        service.addCreditToWallet(500.0, 1);

        assertEquals(1500.0, wallet.getBalance());
        verify(repository).save(wallet);
        verify(transactionService).saveTransaction(any(Transaction.class));
    }

    @Test
    void payFromWallet_shouldTransferMoneyAndSaveTransaction() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setEmail("customer@example.com");

        Expert expert = new Expert();
        expert.setId(2);
        expert.setEmail("expert@example.com");

        Order order = new Order();
        order.setCustomer(customer);
        order.setExpert(expert);

        Suggestion suggestion = new Suggestion();
        suggestion.setPrice(400.0);

        Wallet customerWallet = new Wallet();
        customerWallet.setUser(customer);
        customerWallet.setBalance(1000.0);

        Wallet expertWallet = new Wallet();
        expertWallet.setUser(expert);
        expertWallet.setBalance(100.0);

        when(orderService.findById(anyInt())).thenReturn(order);
        when(suggestionService.findById(anyInt())).thenReturn(suggestion);
        when(repository.findByUserId(1)).thenReturn(Optional.of(customerWallet));
        when(repository.findByUserId(2)).thenReturn(Optional.of(expertWallet));

        service.payFromWallet(5, 6);

        assertEquals(600.0, customerWallet.getBalance());
        assertEquals(500.0, expertWallet.getBalance());

        verify(transactionService).saveTransaction(any(Transaction.class));
    }

    @Test
    void payFromWallet_shouldThrow_whenInsufficientFunds() {
        Customer customer = new Customer();
        customer.setId(1);

        Expert expert = new Expert();
        expert.setId(2);

        Order order = new Order();
        order.setCustomer(customer);
        order.setExpert(expert);

        Suggestion suggestion = new Suggestion();
        suggestion.setPrice(1200.0); // More than wallet

        Wallet customerWallet = new Wallet();
        customerWallet.setUser(customer);
        customerWallet.setBalance(1000.0);

        when(orderService.findById(anyInt())).thenReturn(order);
        when(suggestionService.findById(anyInt())).thenReturn(suggestion);
        when(repository.findByUserId(1)).thenReturn(Optional.of(customerWallet));

        assertThrows(CouldNotUpdateException.class, () -> service.payFromWallet(1, 1));
        verify(repository, never()).save(any());
    }

    @Test
    void findByUserId_shouldReturnWallet_whenExists() {
        when(repository.findByUserId(1)).thenReturn(Optional.of(wallet));
        Wallet result = service.findByUserId(1);
        assertEquals(wallet, result);
    }

    @Test
    void findByUserId_shouldThrow_whenNotExists() {
        when(repository.findByUserId(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findByUserId(1));
    }
}
