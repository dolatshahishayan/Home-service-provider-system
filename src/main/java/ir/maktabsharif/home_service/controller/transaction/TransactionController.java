package ir.maktabsharif.home_service.controller.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction controller",description = "Controller class for transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/find-by-user-id")
    @Operation(summary = "Find by user id",description = "Finds all transactions by user id")
    public ResponseEntity<?> findTransactionsBySenderId(HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(transactionService.findByUserId(currentUser.getUserId()));
    }

}
