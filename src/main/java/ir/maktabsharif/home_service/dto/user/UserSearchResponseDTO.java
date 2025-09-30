package ir.maktabsharif.home_service.dto.user;

import ir.maktabsharif.home_service.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class UserSearchResponseDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Double score;
    private String expertStatus;

    public UserSearchResponseDTO() {
    }

    public UserSearchResponseDTO(Integer id, String firstName, String lastName, String email, Role role, Double score, String expertStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.score = score;
        this.expertStatus = expertStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getExpertStatus() {
        return expertStatus;
    }

    public void setExpertStatus(String expertStatus) {
        this.expertStatus = expertStatus;
    }
}
