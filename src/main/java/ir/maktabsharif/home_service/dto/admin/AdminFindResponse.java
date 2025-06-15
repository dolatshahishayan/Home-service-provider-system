package ir.maktabsharif.home_service.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminFindResponse {
    private Integer id;
    private String firstName;
    private String lastName;
}
