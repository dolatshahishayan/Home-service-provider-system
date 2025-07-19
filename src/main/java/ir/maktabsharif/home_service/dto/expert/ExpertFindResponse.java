package ir.maktabsharif.home_service.dto.expert;

import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertFindResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private ExpertStatus expertStatus;
    private Double score;
    private byte[] profilePictureData;
    private Boolean isEmailVerified;
}
