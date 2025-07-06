package ir.maktabsharif.home_service.dto.expert;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.save.class})
    @Null(groups = {ValidationGroup.update.class})
    private String firstName;
    @NotBlank(groups = {ValidationGroup.save.class})
    @Null(groups = {ValidationGroup.update.class})
    private String lastName;
    @NotBlank(groups = {ValidationGroup.save.class})
    @Email
    private String email;
    @NotBlank(groups = {ValidationGroup.save.class})
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must be at least 8 characters long and contain both letters and numbers.")
    private String password;
    private Double score;
    private String imagePath;
}
