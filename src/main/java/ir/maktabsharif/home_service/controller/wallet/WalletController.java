package ir.maktabsharif.home_service.controller.wallet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.payment.PaymentRequestDTO;
import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.RecaptchaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallets controller", description = "Controller class for wallets")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;
    private final RecaptchaUtil recaptchaUtil;

    @PostMapping("/save")
    @Operation(summary = "Save wallet", description = "Method for saving a wallet")
    public ResponseEntity<WalletFindResponse> save(@RequestBody @Validated(ValidationGroup.Save.class) WalletSaveUpdateRequest walletSaveUpdateRequest) {
        Wallet wallet = walletService.saveWithDTO(walletSaveUpdateRequest);
        return ResponseEntity.ok(walletMapper.mapToResponse(wallet));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/add-credit-to-wallet")
    @Operation(summary = "Add credit to wallet", description = "Method for adding credit to wallet")
    public ResponseEntity<String> addCreditToWallet(@RequestBody PaymentRequestDTO dto) {
        if (!recaptchaUtil.isValid(dto.getRecaptcha())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Captcha is not valid");
        }
        if (dto.getClientTimeLeft() <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client time left is invalid");
        }
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        walletService.addCreditToWallet(dto.getAmount(), dto.getTransactionId(), principal.user().getId());
        return ResponseEntity.ok("Added credit to wallet");
    }

    @GetMapping("/find-by-user-id")
    @Operation(summary = "Find by user id", description = "Finds a wallet with given user id")
    public ResponseEntity<WalletFindResponse> findByUserId(@RequestParam Integer userId) {
        Wallet byUserId = walletService.findByUserId(userId);
        return ResponseEntity.ok(walletMapper.mapToResponse(byUserId));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/pay-from-wallet")
    @Operation(summary = "Pay from wallet", description = "Method for paying from wallet")
    public ResponseEntity<WalletFindResponse> payFromWallet(@RequestParam Integer orderId) {
        Wallet wallet = walletService.payFromWallet(orderId);
        return ResponseEntity.ok(walletMapper.mapToResponse(wallet));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT','ROLE_CUSTOMER')")
    @GetMapping("/get-balance")
    @Operation(summary = "Get balance", description = "Get wallet's current balance")
    public ResponseEntity<Double> getCurrentBalance() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(walletService.getCurrentBalance(principal.user().getId()));
    }
}
