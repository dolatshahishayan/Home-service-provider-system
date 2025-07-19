package ir.maktabsharif.home_service.service.wallet;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.model.wallet.Wallet;

public interface WalletService extends BaseService<Wallet, Integer> {
    void addCreditToWallet(Double credit);

    Wallet findByUserId(Integer userId);

    Wallet payFromWallet(Integer orderId);

    Wallet saveWithDTO(WalletSaveUpdateRequest walletSaveUpdateRequest);

    Double getCurrentBalance();
}
