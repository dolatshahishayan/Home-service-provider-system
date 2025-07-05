package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class WalletServiceImpl extends BaseServiceImpl<Wallet, Integer, WalletRepository, WalletMapper> implements WalletService {
    protected final UserService userService;

    public WalletServiceImpl(WalletRepository repository, WalletMapper mapper, UserService userService) {
        super(repository, mapper);
        this.userService = userService;
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
    }

    @Override
    public void payFromWallet(Order order, Suggestion suggestion) {
        Wallet wallet = findByUserId(order.getCustomer().getId());
        Double price = suggestion.getPrice();
        if (wallet.getBalance() < price) {
            throw new CouldNotUpdateException("Insufficient funds.");
        }
        Double newBalance = wallet.getBalance() - price;
        wallet.setBalance(newBalance);
        save(wallet);

        Wallet expertWallet = findByUserId(order.getExpert().getId());
        expertWallet.setBalance(expertWallet.getBalance() + price);
        save(expertWallet);
    }

    @Override
    public Wallet findByUserId(Integer userId) {
        return repository.findByUserId(userId).orElseThrow(NoElementFoundException::new);
    }
}
