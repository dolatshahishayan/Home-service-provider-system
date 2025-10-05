package ir.maktabsharif.home_service.model.transaction;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.enums.TransactionStatus;
import ir.maktabsharif.home_service.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {

    private BigDecimal amount;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private LocalDateTime expireDate;

}
