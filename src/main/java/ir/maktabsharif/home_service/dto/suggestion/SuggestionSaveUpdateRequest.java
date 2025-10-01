package ir.maktabsharif.home_service.dto.suggestion;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Integer orderId;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String description;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double price;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double workDuration;
    @NotNull(groups = {ValidationGroup.Save.class})
    private LocalDateTime startDate;
    private Boolean accepted;

}
