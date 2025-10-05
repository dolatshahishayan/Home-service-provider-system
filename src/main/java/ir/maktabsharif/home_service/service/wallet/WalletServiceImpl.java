package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InsufficientFundsException;
import ir.maktabsharif.home_service.exception.InvalidRequestException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.TransactionStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class WalletServiceImpl extends BaseServiceImpl<Wallet, Integer, WalletRepository, WalletMapper> implements WalletService {
    protected final UserService userService;
    protected final OrderService orderService;
    protected final SuggestionService suggestionService;
    protected final TransactionService transactionService;

    public WalletServiceImpl(WalletRepository repository, WalletMapper walletMapper, UserService userService, @Lazy OrderService orderService, SuggestionService suggestionService, TransactionService transactionService) {
        super(repository, walletMapper);
        this.userService = userService;
        this.orderService = orderService;
        this.suggestionService = suggestionService;
        this.transactionService = transactionService;
    }

    @Override
    public Wallet saveWithDTO(WalletSaveUpdateRequest walletSaveUpdateRequest) {
        Wallet wallet = new Wallet();
        wallet.setUser(userService.findById(walletSaveUpdateRequest.getUserId()));
        wallet.setBalance(BigDecimal.ZERO);
        return save(wallet);
    }

    @Override
    public void addCreditToWallet(Double credit, Integer transactionId,Integer userId) {
        User principal = userService.findById(userId);
        Wallet wallet = findByUserId(principal.getId());
        wallet.setBalance(BigDecimal.valueOf(wallet.getBalance().doubleValue() + credit));
        save(wallet);
        Transaction transaction = transactionService.findById(transactionId);
        if (transaction.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Time is expired");
        }
        getTransaction(transaction, principal, principal, credit, TransactionStatus.COMPLETED);
    }

    private void getTransaction(Transaction transaction, User sender, User receiver, Double credit, TransactionStatus completed) {
        transaction.setSender(userService.findById(sender.getId()));
        transaction.setReceiver(userService.findById(receiver.getId()));
        transaction.setAmount(BigDecimal.valueOf(credit));
        transaction.setStatus(completed);
        transactionService.saveTransaction(transaction);
    }

    @Override
    public Wallet payFromWallet(Integer orderId) {
        Order order = orderService.findById(orderId);
        Wallet wallet = findByUserId(order.getCustomer().getId());
        if (wallet.getBalance().doubleValue()< order.getFinalPrice().doubleValue()) {
            Transaction transaction = new Transaction();
            getTransaction(transaction, order.getCustomer(), order.getExpert(), order.getFinalPrice().doubleValue(), TransactionStatus.FAILED);
            throw new InsufficientFundsException("Insufficient funds. Please deposit " + (order.getFinalPrice().doubleValue() - wallet.getBalance().doubleValue()) + " to your wallet.");
        }
        double expertShare = (order.getFinalPrice().doubleValue() * 70) / 100;
        Transaction transaction = new Transaction();
        getTransaction(transaction, order.getCustomer(), order.getExpert(), expertShare, TransactionStatus.COMPLETED);
        return getExpertAndOrder(wallet, order.getFinalPrice().doubleValue(), order);
    }

    private Wallet getExpertAndOrder(Wallet customerWallet, Double price, Order order) {
        Double newBalance = customerWallet.getBalance().doubleValue() - price;
        customerWallet.setBalance( BigDecimal.valueOf(newBalance));
        Wallet saved = save(customerWallet);
        double expertShare = (price * 70) / 100;
        Wallet expertWallet = findByUserId(order.getExpert().getId());
        expertWallet.setBalance(BigDecimal.valueOf(expertWallet.getBalance().doubleValue() + expertShare));
        save(expertWallet);
        order.setOrderStatus(OrderStatus.PAYED);
        orderService.save(order);
        return saved;
    }

    @Override
    public Wallet findByUserId(Integer userId) {
        return repository.findByUserId(userId).orElseThrow(NoElementFoundException::new);
    }

    @Override
    public Double getCurrentBalance(Integer userId) {
        User principal = userService.findById(userId);
        Wallet wallet = findByUserId(principal.getId());
        return wallet.getBalance().doubleValue();
    }
}
