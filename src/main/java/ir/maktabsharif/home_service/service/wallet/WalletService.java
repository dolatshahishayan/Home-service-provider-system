package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.wallet.Wallet;

public interface WalletService extends BaseService<Wallet, Integer> {
    void addCreditToWallet(Double credit, Integer userId);

    Wallet findByUserId(Integer userId);

    void payFromWallet(Order order, Suggestion suggestion);

    Wallet saveWithDTO(WalletSaveUpdateRequest walletSaveUpdateRequest);
}
