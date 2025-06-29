package ir.maktabsharif.home_service.dto.suggestion;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotNull
    private Integer expertId;
    @NotNull
    private Integer orderId;
    @NotBlank
    private String description;
    @NotNull
    private Double price;
    @NotNull
    private Double workDuration;
    @NotNull
    private LocalDateTime startDate;
    private Boolean accepted;
}
