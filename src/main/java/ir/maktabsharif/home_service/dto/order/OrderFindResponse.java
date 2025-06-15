package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.subservice.SubServiceFindResponse;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class OrderFindResponse {
    private Integer id;
    private String description;
    private Double proposedPrice;
    private LocalDateTime startDate;
    private String address;
    private OrderStatus orderStatus;
    private CustomerFindResponse customer;
    private SubServiceFindResponse subService;
}
