package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InsufficientFundsException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    WalletServiceImpl walletService;


    @Test
    void saveWithDTO_ShouldSaveWalletWithZeroBalance() {
        WalletSaveUpdateRequest request = new WalletSaveUpdateRequest();
        request.setUserId(1);
        User user = new User();
        when(userService.findById(1)).thenReturn(user);

        Wallet savedWallet = new Wallet();
        savedWallet.setUser(user);
        savedWallet.setBalance(0.0);
        when(repository.save(any())).thenReturn(savedWallet);

        Wallet result = walletService.saveWithDTO(request);

        assertEquals(0.0, result.getBalance());
        assertEquals(user, result.getUser());
        verify(repository).save(any(Wallet.class));
        verify(userService).findById(1);
    }

    @Test
    void addCreditToWallet_ShouldIncreaseBalanceAndSaveTransaction() {
        Integer userId = 1;
        Double credit = 100.0;

        Wallet wallet = new Wallet();
        wallet.setBalance(50.0);
        User user = new User();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(userService.findById(userId)).thenReturn(user);
        when(repository.save(wallet)).thenReturn(wallet);
        doNothing().when(transactionService).saveTransaction(any(Transaction.class));

        walletService.addCreditToWallet(credit, userId);

        assertEquals(150.0, wallet.getBalance());
        verify(repository).save(wallet);
        verify(transactionService).saveTransaction(any(Transaction.class));
        verify(userService, times(2)).findById(userId);
    }

    @Test
    void payFromWallet_ShouldDeductPriceFromCustomerAndAddToExpert() {
        Integer orderId = 1;
        Customer customer = new Customer();
        customer.setId(1);
        Expert expert = new Expert();
        expert.setId(2);

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setExpert(expert);
        order.setFinalPrice(200.0);
        order.setOrderStatus(OrderStatus.WAITING_FOR_EXPERT_SUGGESTION);

        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(500.0);
        Wallet expertWallet = new Wallet();
        expertWallet.setBalance(300.0);

        when(orderService.findById(orderId)).thenReturn(order);
        when(repository.findByUserId(customer.getId())).thenReturn(Optional.of(customerWallet));
        when(repository.findByUserId(expert.getId())).thenReturn(Optional.of(expertWallet));        when(repository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(orderService.save(order)).thenReturn(order);
        doNothing().when(transactionService).saveTransaction(any(Transaction.class));

        Wallet updatedWallet = walletService.payFromWallet(orderId);

        assertEquals(300.0, updatedWallet.getBalance());
        assertEquals(440.0, expertWallet.getBalance());

        assertEquals(OrderStatus.PAYED, order.getOrderStatus());

        verify(repository, times(2)).save(any(Wallet.class));
        verify(orderService).save(order);
        verify(transactionService).saveTransaction(any(Transaction.class));
    }

    @Test
    void payFromWallet_ShouldThrow_WhenInsufficientFunds() {
        Integer orderId = 1;
        Customer customer = new Customer();
        customer.setId(1);
        Expert expert = new Expert();
        expert.setId(2);

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setExpert(expert);
        order.setFinalPrice(200.0);

        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(100.0);

        when(orderService.findById(orderId)).thenReturn(order);
        doNothing().when(transactionService).saveTransaction(any(Transaction.class));
        when(repository.findByUserId(customer.getId())).thenReturn(Optional.of(customerWallet));
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> walletService.payFromWallet(orderId));

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        verify(repository, never()).save(any());
    }

    @Test
    void findByUserId_ShouldReturnWallet_WhenFound() {
        Integer userId = 1;
        Wallet wallet = new Wallet();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        Wallet result = walletService.findByUserId(userId);
        assertSame(wallet, result);
        verify(repository).findByUserId(userId);
    }

    @Test
    void findByUserId_ShouldThrow_WhenNotFound() {
        Integer userId = 1;
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NoElementFoundException.class, () -> walletService.findByUserId(userId));
        verify(repository).findByUserId(userId);
    }

    @Test
    void getCurrentBalance_ShouldReturnBalance() {
        Integer userId = 1;
        Wallet wallet = new Wallet();
        wallet.setBalance(250.0);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        Double balance = walletService.getCurrentBalance(userId);
        assertEquals(250.0, balance);
    }
}
