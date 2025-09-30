package ir.maktabsharif.home_service.model.user;

import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@NoArgsConstructor
@AllArgsConstructor
public class Expert extends User {

    @Enumerated(EnumType.STRING)
    private ExpertStatus expertStatus;
    private Double score;
    private byte[] profilePictureData;

    public ExpertStatus getExpertStatus() {
        return expertStatus;
    }

    public void setExpertStatus(ExpertStatus expertStatus) {
        this.expertStatus = expertStatus;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public byte[] getProfilePictureData() {
        return profilePictureData;
    }

    public void setProfilePictureData(byte[] profilePictureData) {
        this.profilePictureData = profilePictureData;
    }
}
