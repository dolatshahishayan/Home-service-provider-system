package ir.maktabsharif.home_service.model.transaction;

import ir.maktabsharif.home_service.model.enums.TransactionStatus;
import ir.maktabsharif.home_service.model.user.User;
import javax.persistence.*;


import java.time.LocalDateTime;

@Entity


public class Transaction {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    private Double amount;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private LocalDateTime expireDate;

    public Transaction() {
    }

    public Transaction(Integer id, Double amount, User sender, User receiver, LocalDateTime timestamp, TransactionStatus status, LocalDateTime expireDate) {
        this.id = id;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.status = status;
        this.expireDate = expireDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
}
