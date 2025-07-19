package ir.maktabsharif.home_service.controller.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.transaction.TransactionFindResponse;
import ir.maktabsharif.home_service.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions controller",description = "Controller class for transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT','ROLE_CUSTOMER')")
    @GetMapping("/find-by-user")
    @Operation(summary = "Find by user",description = "Finds all transactions by user")
    public ResponseEntity<Page<TransactionFindResponse>> findTransactionsByUser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.findByUserId(PageRequest.of(page, size)));
    }

}
