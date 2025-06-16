package ir.maktabsharif.home_service.dto.expert_service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expert_ServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotNull
    private Integer expertId;
    @NotNull
    private Integer serviceId;
}
