package ir.maktabsharif.home_service.dto.user;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.enums.Role;
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
public class UserSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String firstName;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String lastName;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String email;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String password;
    @NotNull
    private Role role;
}
