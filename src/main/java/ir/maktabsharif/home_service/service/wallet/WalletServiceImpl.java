package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.CouldNotUpdateException;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.repository.wallet.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl extends BaseServiceImpl<Wallet, WalletRepository, WalletMapper> implements WalletService {
    public WalletServiceImpl(WalletRepository repository, WalletMapper mapper) {
        super(repository, mapper);
    }
    @Override
    public void saveWithDTO(WalletSaveUpdateRequest walletSaveUpdateRequest) {
        save(mapper.mapToEntity(walletSaveUpdateRequest));
    }
    @Override
    public void addCreditToWallet(Double credit, Integer userId) {
        Wallet wallet = findByUserId(userId);
        wallet.setBalance(wallet.getBalance() + credit);
        update(wallet);
    }
    @Override
    public void saveWithExpert(Expert expert) {
        WalletSaveUpdateRequest walletSaveUpdateRequest = new WalletSaveUpdateRequest();
        walletSaveUpdateRequest.setBalance(0.0);
        walletSaveUpdateRequest.setUserId(expert.getId());
        saveWithDTO(walletSaveUpdateRequest);
    }

    @Override
    public void payFromWallet(Order order, Suggestion suggestion) {
        Wallet wallet=findByUserId(order.getCustomer().getId());
        Double price=suggestion.getPrice();
        if (wallet.getBalance()<price) {
            throw new CouldNotUpdateException("Insufficient funds.");
        }
        Double newBalance=wallet.getBalance()-price;
        wallet.setBalance(newBalance);
        update(wallet);

        Wallet expertWallet=findByUserId(order.getExpert().getId());
        expertWallet.setBalance(expertWallet.getBalance()+price);
        update(expertWallet);
    }

    @Override
    public Wallet findByUserId(Integer userId) {
        return repository.findByUserId(userId);
    }
}
