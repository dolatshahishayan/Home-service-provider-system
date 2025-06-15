package ir.maktabsharif.home_service.dto.suggestion;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.expert.ExpertFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SuggestionFindResponse {
    private Integer id;
    private ExpertFindResponse expert;
    private OrderFindResponse order;
    private String description;
    private Double price;
    private Double workDuration;
}
