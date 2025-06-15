package ir.maktabsharif.home_service.dto.subservice;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubServiceFindResponse {
    private Integer id;
    private String name;
    private Double basePrice;
    private String description;
    private ServiceFindResponse parentService;
}
