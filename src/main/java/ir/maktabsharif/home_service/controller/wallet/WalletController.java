package ir.maktabsharif.home_service.controller.wallet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.payment.PaymentRequestDTO;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.dto.wallet.WalletFindResponse;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.wallet.WalletMapper;
import ir.maktabsharif.home_service.model.wallet.Wallet;
import ir.maktabsharif.home_service.service.recaptcha.RecaptchaService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet controller", description = "Controller class for wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;
    private final RecaptchaService recaptchaService;

    @PostMapping("/save")
    @Operation(summary = "Save wallet", description = "Method for saving a wallet")
    public ResponseEntity<WalletFindResponse> save(@RequestBody @Validated(ValidationGroup.Save.class) WalletSaveUpdateRequest walletSaveUpdateRequest) {
        Wallet wallet = walletService.saveWithDTO(walletSaveUpdateRequest);
        return ResponseEntity.ok(walletMapper.mapToResponse(wallet));
    }

    @PutMapping("/add-credit-to-wallet")
    @Operation(summary = "Add credit to wallet", description = "Method for adding credit to wallet")
    public ResponseEntity<String> addCreditToWallet(@RequestBody PaymentRequestDTO dto, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }
        if (!recaptchaService.isValid(dto.getRecaptcha())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Captcha is not valid");
        }
        if (dto.getCardNumber().length() != 16) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Card number is not valid");
        }
        if (dto.getClientTimeLeft()<=0){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client time left is invalid");
        }
        walletService.addCreditToWallet(dto.getAmount(),currentUser.getUserId());
        return ResponseEntity.ok("Added credit to wallet");
    }

    @GetMapping("/find-by-user-id")
    @Operation(summary = "Find by user id", description = "Finds a wallet with given user id")
    public ResponseEntity<WalletFindResponse> findByUserId(@RequestParam Integer userId) {
        Wallet byUserId = walletService.findByUserId(userId);
        return ResponseEntity.ok(walletMapper.mapToResponse(byUserId));
    }

    @PutMapping("/pay-from-wallet")
    @Operation(summary = "Pay from wallet", description = "Method for paying from wallet")
    public ResponseEntity<WalletFindResponse> payOrder(@RequestParam Integer orderId) {
        Wallet wallet = walletService.payOrder(orderId);
        return ResponseEntity.ok(walletMapper.mapToResponse(wallet));
    }

    @GetMapping("/get-balance")
    @Operation(summary = "Get balance", description = "Get wallet's current balance")
    public ResponseEntity<?> getCurrentBalance(HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(walletService.getCurrentBalance(currentUser.getUserId()));
    }
}
