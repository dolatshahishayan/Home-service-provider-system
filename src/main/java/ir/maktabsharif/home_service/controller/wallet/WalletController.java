package ir.maktabsharif.home_service.controller.wallet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet controller",description = "Controller class for wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    @PostMapping("/save")
    @Operation(summary = "Save wallet",description = "Method for saving a wallet")
    public ResponseEntity<WalletFindResponse> save(@RequestBody @Validated(ValidationGroup.save.class)WalletSaveUpdateRequest walletSaveUpdateRequest) {
        Wallet wallet = walletService.saveWithDTO(walletSaveUpdateRequest);
        return ResponseEntity.ok(walletMapper.mapToResponse(wallet));
    }

    @PutMapping("/add-credit-to-wallet")
    @Operation(summary = "Add credit to wallet",description = "Method for adding credit to wallet")
    public ResponseEntity<String> addCreditToWallet(@RequestParam Double credit, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        walletService.addCreditToWallet(credit, currentUser.getUserId());
        return ResponseEntity.ok("Added credit to wallet");
    }

    @GetMapping("/find-by-user-id")
    @Operation(summary = "Find by user id",description = "Finds a wallet with given user id")
    public ResponseEntity<WalletFindResponse> findByUserId(@RequestParam Integer userId) {
        Wallet byUserId = walletService.findByUserId(userId);
        return ResponseEntity.ok(walletMapper.mapToResponse(byUserId));
    }

    @PutMapping("/pay-from-wallet")
    @Operation(summary = "Pay from wallet",description = "Method for paying from wallet")
    public ResponseEntity<String> payFromWallet(@RequestParam Integer orderId, @RequestParam Integer suggestionId) {
        walletService.payFromWallet(orderId, suggestionId);
        return ResponseEntity.ok("Payed from wallet");
    }
}
