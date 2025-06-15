package ir.maktabsharif.home_service.dto.expert;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpertFindResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private ExpertStatus expertStatus;
    private byte[] profilePicture;
    private String expertise;
}
