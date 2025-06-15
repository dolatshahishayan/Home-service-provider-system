package ir.maktabsharif.home_service.dto.admin;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class AdminSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    private String firstName;
    private String lastName;
    @NotBlank(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private String email;
    @NotBlank(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private String password;
}
