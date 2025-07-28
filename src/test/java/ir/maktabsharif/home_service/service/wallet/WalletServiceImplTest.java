package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InsufficientFundsException;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.TransactionStatus;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    WalletServiceImpl walletService;

    @Mock
    WalletRepository walletRepository;

    @Mock
    UserService userService;

    @Mock
    OrderService orderService;

    @Mock
    TransactionService transactionService;


    @Test
    void saveWithDTO_shouldCreateWalletWithZeroBalance() {
        WalletSaveUpdateRequest request = new WalletSaveUpdateRequest();
        request.setUserId(1);

        User user = new User();
        user.setId(1);

        when(userService.findById(1)).thenReturn(user);
        when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet wallet = walletService.saveWithDTO(request);

        assertNotNull(wallet);
        assertEquals(user, wallet.getUser());
        assertEquals(0.0, wallet.getBalance());
        verify(walletRepository).save(any());
    }

    @Test
    void addCreditToWallet_shouldIncreaseBalanceAndSaveTransaction() {
        Integer userId = 1;
        Integer transactionId = 100;
        Double credit = 50.0;

        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(100.0);

        Transaction transaction = new Transaction();
        transaction.setExpireDate(LocalDateTime.now().plusMinutes(5));

        when(userService.findById(userId)).thenReturn(user);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionService.findById(transactionId)).thenReturn(transaction);

        walletService.addCreditToWallet(credit, transactionId, userId);

        assertEquals(150.0, wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionService, times(1)).saveTransaction(transaction);
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals(user, transaction.getSender());
        assertEquals(user, transaction.getReceiver());
        assertEquals(credit, transaction.getAmount());
    }

    @Test
    void addCreditToWallet_expiredTransaction_shouldThrow() {
        Integer userId = 1;
        Integer transactionId = 100;
        Double credit = 50.0;

        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(100.0);

        Transaction transaction = new Transaction();
        transaction.setExpireDate(LocalDateTime.now().minusMinutes(5));

        when(userService.findById(userId)).thenReturn(user);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(transactionService.findById(transactionId)).thenReturn(transaction);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> walletService.addCreditToWallet(credit, transactionId, userId));
        assertEquals("Time is expired", ex.getMessage());
    }

    @Test
    void payFromWallet_insufficientFunds_shouldThrowAndSaveFailedTransaction() {
        Integer orderId = 10;

        Customer customer = new Customer();
        customer.setId(1);
        Expert expert = new Expert();
        expert.setId(2);

        Order order = new Order();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setExpert(expert);
        order.setFinalPrice(200.0);

        Wallet wallet = new Wallet();
        wallet.setUser(customer);
        wallet.setBalance(100.0);

        when(orderService.findById(orderId)).thenReturn(order);
        when(walletRepository.findByUserId(customer.getId())).thenReturn(Optional.of(wallet));
        doNothing().when(transactionService).saveTransaction(any());

        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class,
                () -> walletService.payFromWallet(orderId));

        assertTrue(ex.getMessage().contains("Insufficient funds"));
        verify(transactionService).saveTransaction(any());
    }

    @Test
    void payFromWallet_sufficientFunds_shouldPayAndUpdateWalletsAndOrder() {
        Integer orderId = 10;

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
        customerWallet.setUser(customer);
        customerWallet.setBalance(300.0);

        Wallet expertWallet = new Wallet();
        expertWallet.setUser(expert);
        expertWallet.setBalance(50.0);

        when(orderService.findById(orderId)).thenReturn(order);
        when(walletRepository.findByUserId(customer.getId())).thenReturn(Optional.of(customerWallet));
        when(walletRepository.findByUserId(expert.getId())).thenReturn(Optional.of(expertWallet));
        when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet resultWallet = walletService.payFromWallet(orderId);

        // Customer wallet balance decreased by finalPrice
        assertEquals(100.0, resultWallet.getBalance());
        // Expert wallet balance increased by 70% of finalPrice
        assertEquals(50.0 + (200.0 * 0.7), expertWallet.getBalance());
        assertEquals(OrderStatus.PAYED, order.getOrderStatus());

        verify(walletRepository, times(2)).save(any());
        verify(orderService, times(1)).save(order);
    }

    @Test
    void findByUserId_walletFound() {
        Integer userId = 1;
        Wallet wallet = new Wallet();
        wallet.setUser(new User());
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.findByUserId(userId);

        assertEquals(wallet, result);
    }

    @Test
    void findByUserId_walletNotFound_throws() {
        Integer userId = 1;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NoElementFoundException.class, () -> walletService.findByUserId(userId));
    }

    @Test
    void getCurrentBalance_shouldReturnBalance() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setBalance(123.45);
        wallet.setUser(user);

        when(userService.findById(userId)).thenReturn(user);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Double balance = walletService.getCurrentBalance(userId);

        assertEquals(123.45, balance);
    }
}
