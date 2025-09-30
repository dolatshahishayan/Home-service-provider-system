package ir.maktabsharif.home_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public class PaymentRequestDTO {
    private Integer transactionId;
    private Double amount;
    private String cardNumber;
    private String cvv2;
    private String expirationDate;
    private String otp;
    private String recaptcha;
    private int clientTimeLeft;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Integer transactionId, Double amount, String cardNumber, String cvv2, String expirationDate, String otp, String recaptcha, int clientTimeLeft) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.cvv2 = cvv2;
        this.expirationDate = expirationDate;
        this.otp = otp;
        this.recaptcha = recaptcha;
        this.clientTimeLeft = clientTimeLeft;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv2() {
        return cvv2;
    }

    public void setCvv2(String cvv2) {
        this.cvv2 = cvv2;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRecaptcha() {
        return recaptcha;
    }

    public void setRecaptcha(String recaptcha) {
        this.recaptcha = recaptcha;
    }

    public int getClientTimeLeft() {
        return clientTimeLeft;
    }

    public void setClientTimeLeft(int clientTimeLeft) {
        this.clientTimeLeft = clientTimeLeft;
    }
}
