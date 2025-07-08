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
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String name;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double basePrice;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String description;
    private Integer parentServiceId;
}
