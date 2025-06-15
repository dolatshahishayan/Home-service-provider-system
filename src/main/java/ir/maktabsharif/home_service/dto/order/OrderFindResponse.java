package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.model.enums.OrderStatus;

import java.time.LocalDateTime;

public class OrderFindResponse {
    private Integer id;
    private String description;
    private Double proposedPrice;
    private LocalDateTime startDate;
    private String address;
    private OrderStatus orderStatus;
    private Integer customerId;
    private Integer subServiceId;
}
