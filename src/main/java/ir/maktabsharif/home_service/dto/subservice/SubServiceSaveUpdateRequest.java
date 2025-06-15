package ir.maktabsharif.home_service.dto.subservice;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import ir.maktabsharif.home_service.model.service.Service;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    private Double basePrice;
    @NotBlank
    private String description;
    @NotNull
    private ServiceFindResponse parentService;
}
