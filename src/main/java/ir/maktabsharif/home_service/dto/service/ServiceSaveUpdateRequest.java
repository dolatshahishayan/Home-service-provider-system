package ir.maktabsharif.home_service.dto.service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.service.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
