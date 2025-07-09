package ir.maktabsharif.home_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private String cardNumber;
    private String cvv2;
    private String otp;
    private String recaptcha;
    private int clientTimeLeft;
    private Integer orderId;
}
