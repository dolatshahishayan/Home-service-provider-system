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
public class ExpertServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private Integer expertId;
    @NotNull(groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private Integer serviceId;
}
