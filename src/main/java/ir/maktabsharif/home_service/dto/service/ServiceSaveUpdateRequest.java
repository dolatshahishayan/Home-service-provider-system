package ir.maktabsharif.home_service.dto.service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.update.class, ValidationGroup.save.class})
    private String name;
    @NotNull
    private Double basePrice;
    @NotBlank
    private String description;
    private Integer parentServiceId;
}
