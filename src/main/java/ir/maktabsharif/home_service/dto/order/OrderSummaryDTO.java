package ir.maktabsharif.home_service.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Integer id;
    private Integer serviceId;
    private Integer customerId;
    private LocalDateTime startDate;
    private String address;
}
