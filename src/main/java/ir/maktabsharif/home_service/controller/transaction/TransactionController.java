package ir.maktabsharif.home_service.controller.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.model.transaction.Transaction;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction controller",description = "Controller class for transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/find-by-sender-id")
    @Operation(summary = "Find by sender id",description = "Finds all transactions by sender id")
    public ResponseEntity<List<Transaction>> findTransactionsBySenderId(@RequestParam Integer senderId) {
        return ResponseEntity.ok(transactionService.findBySenderId(senderId));
    }

    @GetMapping("/find-by-receiver-id")
    @Operation(summary = "Find by receiver id",description = "Finds all transactions by receiver id")
    public ResponseEntity<List<Transaction>> findTransactionsByReceiverId(@RequestParam Integer receiverId) {
        return ResponseEntity.ok(transactionService.findByReceiverId(receiverId));
    }
}
