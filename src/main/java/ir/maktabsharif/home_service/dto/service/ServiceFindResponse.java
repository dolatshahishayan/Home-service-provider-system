package ir.maktabsharif.home_service.dto.service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.subservice.SubServiceFindResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ServiceFindResponse {
    private Integer id;
    private String name;
    private Double basePrice;
    private String description;
    private List<SubServiceFindResponse> subServices;
}
