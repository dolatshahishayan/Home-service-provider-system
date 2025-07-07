package ir.maktabsharif.home_service.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSaveUpdateRequest {
    @NotNull
    @Min(0)
    private Double amount;
    @NotNull
    private Integer senderId;
    @NotNull
    private Integer receiverId;
}
