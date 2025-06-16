package ir.maktabsharif.home_service.dto.admin;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    private String firstName;
    private String lastName;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
