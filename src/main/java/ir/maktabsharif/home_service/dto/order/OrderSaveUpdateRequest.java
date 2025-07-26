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
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String description;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double proposedPrice;
    @FutureOrPresent
    private LocalDateTime startDate;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String address;
    @NotNull(groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private Integer serviceId;
    private Double finalPrice;
}
