package ir.maktabsharif.home_service.dto.expert_subservice;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.subservice.SubServiceFindResponse;
import jakarta.validation.constraints.NotNull;

public class Expert_SubServiceFindResponse {
    private Integer id;
    private ExpertFindResponse expert;
    private SubServiceFindResponse subService;
}
