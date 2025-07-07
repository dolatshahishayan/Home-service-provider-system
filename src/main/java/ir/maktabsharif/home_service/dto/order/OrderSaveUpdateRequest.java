package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.save.class})
    private String description;
    @NotNull(groups = {ValidationGroup.save.class})
    private Double proposedPrice;
    @FutureOrPresent
    private LocalDateTime startDate;
    @NotBlank(groups = {ValidationGroup.save.class})
    private String address;
    @NotNull
    private Integer serviceId;
    private Integer expertId;
}
