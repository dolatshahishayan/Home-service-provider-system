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
public class OrderFindResponse {
    private Integer id;
    private String description;
    private Double proposedPrice;
    private LocalDateTime startDate;
    private String address;
    private OrderStatus orderStatus;
    private Integer customerId;
    private Integer serviceId;
    private Integer expertId;
    private LocalDateTime creationDate;
}
