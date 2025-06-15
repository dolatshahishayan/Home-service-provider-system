package ir.maktabsharif.home_service.dto.expert_service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotNull;

public class Expert_ServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotNull
    private Integer expertId;
    @NotNull
    private Integer subServiceId;
}
