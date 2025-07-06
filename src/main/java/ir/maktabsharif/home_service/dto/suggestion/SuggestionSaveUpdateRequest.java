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
    @NotBlank(groups = {ValidationGroup.save.class})
    private String description;
    @NotNull(groups = {ValidationGroup.save.class})
    private Double price;
    @NotNull(groups = {ValidationGroup.save.class})
    private Double workDuration;
    @NotNull(groups = {ValidationGroup.save.class})
    private LocalDateTime startDate;
    private Boolean accepted;
}
