package ir.maktabsharif.home_service.model.user;

import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity


public class Expert extends User {

    @Enumerated(EnumType.STRING)
    private ExpertStatus expertStatus;
    private Double score;
    private byte[] profilePictureData;

    public Expert() {
    }

    public Expert(Integer id, String firstName, String lastName, String email, String password, LocalDateTime registrationDate, Role role, Boolean isEmailVerified, ExpertStatus expertStatus, Double score, byte[] profilePictureData) {
        super(id, firstName, lastName, email, password, registrationDate, role, isEmailVerified);
        this.expertStatus = expertStatus;
        this.score = score;
        this.profilePictureData = profilePictureData;
    }

    public Expert(ExpertStatus expertStatus, Double score, byte[] profilePictureData) {
        this.expertStatus = expertStatus;
        this.score = score;
        this.profilePictureData = profilePictureData;
    }

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
