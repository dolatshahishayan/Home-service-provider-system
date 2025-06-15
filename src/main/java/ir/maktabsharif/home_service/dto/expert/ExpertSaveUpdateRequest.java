package ir.maktabsharif.home_service.dto.expert;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpertSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.update.class})
    private Integer id;
    private String firstName;
    private String lastName;
    @NotBlank(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private String email;
    @NotBlank(groups = {ValidationGroup.update.class,ValidationGroup.save.class})
    private String password;
    private ExpertStatus expertStatus;
    //add annotation
    private byte[] profilePicture;
}
