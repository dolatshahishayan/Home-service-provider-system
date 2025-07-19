package ir.maktabsharif.home_service.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFindResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private Boolean isEmailVerified;
}
