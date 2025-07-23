package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchRequest {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private OrderStatus status;
    private Integer serviceId;
}
