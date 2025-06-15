package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class OrderSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotBlank
    private String description;
    @NotNull
    private Double proposedPrice;
    @FutureOrPresent
    private LocalDateTime startDate;
    @NotBlank
    private String address;
    @NotNull
    private OrderStatus orderStatus;
    @NotNull
    private Integer customerId;
    @NotNull
    private Integer subServiceId;
}
