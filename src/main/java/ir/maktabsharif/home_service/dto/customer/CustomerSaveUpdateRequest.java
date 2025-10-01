package ir.maktabsharif.home_service.dto.customer;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String firstName;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String lastName;
    @NotBlank(groups = {ValidationGroup.Save.class})
    @Email
    private String email;
    @NotBlank(groups = {ValidationGroup.Save.class})
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must be at least 8 characters long and contain both letters and numbers.",
            groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private String password;

}
