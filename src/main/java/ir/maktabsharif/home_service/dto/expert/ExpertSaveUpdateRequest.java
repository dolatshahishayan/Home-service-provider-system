package ir.maktabsharif.home_service.dto.expert;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
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
public class ExpertSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.save.class})
    private String firstName;
    @NotBlank(groups = {ValidationGroup.save.class})
    private String lastName;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private ExpertStatus expertStatus;
    private byte[] profilePicture;
}
