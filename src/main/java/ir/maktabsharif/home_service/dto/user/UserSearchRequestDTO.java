package ir.maktabsharif.home_service.dto.user;

import ir.maktabsharif.home_service.model.enums.Role;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    public UserSearchRequestDTO() {
    }

    public UserSearchRequestDTO(Role role, String name, List<Integer> serviceIds, Double minScore, Double maxScore) {
        this.role = role;
        this.name = name;
        this.serviceIds = serviceIds;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public @Min(1) @Max(5) Double getMinScore() {
        return minScore;
    }

    public void setMinScore(@Min(1) @Max(5) Double minScore) {
        this.minScore = minScore;
    }

    public @Min(1) @Max(5) Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(@Min(1) @Max(5) Double maxScore) {
        this.maxScore = maxScore;
    }
}
