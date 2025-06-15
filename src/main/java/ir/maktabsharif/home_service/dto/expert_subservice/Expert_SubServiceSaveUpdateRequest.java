package ir.maktabsharif.home_service.dto.expert_subservice;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.subservice.SubServiceFindResponse;
import ir.maktabsharif.home_service.model.subservice.SubService;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Expert_SubServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotNull(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private ExpertFindResponse expert;
    @NotNull(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private SubServiceFindResponse subService;
}
