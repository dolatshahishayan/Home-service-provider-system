package ir.maktabsharif.home_service.dto.admin;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.Email;
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
    @NotBlank(groups = {ValidationGroup.save.class})
    private String firstName;
    @NotBlank(groups = {ValidationGroup.save.class})
    private String lastName;
    @NotBlank(groups = {ValidationGroup.save.class})
    @Email
    private String email;
    @NotBlank(groups = {ValidationGroup.save.class})
    private String password;
}
