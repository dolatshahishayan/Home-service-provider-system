package ir.maktabsharif.home_service.dto.transaction;

import java.time.LocalDateTime;



public class TransactionInitializerDTO {
    private Integer id;
    private LocalDateTime expireDate;

    public TransactionInitializerDTO() {
    }

    public TransactionInitializerDTO(Integer id, LocalDateTime expireDate) {
        this.id = id;
        this.expireDate = expireDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
}
