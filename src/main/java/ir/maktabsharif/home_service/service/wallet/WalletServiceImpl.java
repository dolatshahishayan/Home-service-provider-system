package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.InsufficientFundsException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
        wallet.setBalance(0.0);
        return save(wallet);
    }

    @Override
    public void addCreditToWallet(Double credit, Integer userId) {
        Wallet wallet = findByUserId(userId);
        wallet.setBalance(wallet.getBalance() + credit);
        save(wallet);
        Transaction transaction = new Transaction();
        transaction.setSender(userService.findById(userId));
        transaction.setReceiver(userService.findById(userId));
        transaction.setAmount(credit);
        transactionService.saveTransaction(transaction);
    }

    @Override
    public Wallet payFromWallet(Integer orderId) {
        Order order = orderService.findById(orderId);
        Wallet wallet = findByUserId(order.getCustomer().getId());
        Double price = order.getFinalPrice();
        if (wallet.getBalance() < price) {
            throw new InsufficientFundsException();
        }
        Double newBalance = wallet.getBalance() - price;
        wallet.setBalance(newBalance);
        Wallet saved = save(wallet);

        double expertShare = (price * 70) / 100;
        Wallet expertWallet = findByUserId(order.getExpert().getId());
        expertWallet.setBalance(expertWallet.getBalance() + expertShare);
        save(expertWallet);


        Transaction transaction = new Transaction();
        transaction.setAmount(expertShare);
        transaction.setSender(order.getCustomer());
        transaction.setReceiver(order.getExpert());
        transactionService.saveTransaction(transaction);

        return saved;
    }

    @Override
    public Wallet findByUserId(Integer userId) {
        return repository.findByUserId(userId).orElseThrow(NoElementFoundException::new);
    }

    @Override
    public Double getCurrentBalance(Integer userId) {
        Wallet wallet = findByUserId(userId);
        return wallet.getBalance();
    }
}
