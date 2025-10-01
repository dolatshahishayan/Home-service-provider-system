package ir.maktabsharif.home_service.dto.user;

import ir.maktabsharif.home_service.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Double score;
    private String expertStatus;

}
