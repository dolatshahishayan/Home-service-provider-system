package ir.maktabsharif.home_service.dto.user;

import ir.maktabsharif.home_service.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;


import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequestDTO {
    private Role role;
    private String name;
    private List<Integer> serviceIds;
    @Min(1)
    @Max(5)
    private Double minScore;
    @Min(1)
    @Max(5)
    private Double maxScore;

}
