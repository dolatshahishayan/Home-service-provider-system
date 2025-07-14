package ir.maktabsharif.home_service.dto.transaction;

import ir.maktabsharif.home_service.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFindResponse {
    private Double amount;
    private Integer senderId;
    private Integer receiverId;
    private LocalDateTime timeStamp;
    private TransactionStatus status;
}
