package ir.maktabsharif.home_service.dto.user;

import ir.maktabsharif.home_service.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;



public class UserSessionDTO implements Serializable {
    private Integer userId;
    private String email;
    private Role role;

    public UserSessionDTO() {
    }

    public UserSessionDTO(Integer userId, String email, Role role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
