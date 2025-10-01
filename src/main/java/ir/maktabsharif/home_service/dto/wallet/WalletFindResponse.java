package ir.maktabsharif.home_service.dto.wallet;

public class WalletFindResponse {
    private Integer id;
    private Double balance;
    private Integer userId;

    public WalletFindResponse() {
    }

    public WalletFindResponse(Integer id, Double balance, Integer userId) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
