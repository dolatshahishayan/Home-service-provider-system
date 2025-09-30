package ir.maktabsharif.home_service.dto.expert;

import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public class ExpertFindResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private ExpertStatus expertStatus;
    private Double score;
    private byte[] profilePictureData;
    private Boolean isEmailVerified;

    public ExpertFindResponse() {
    }

    public ExpertFindResponse(Integer id, String firstName, String lastName, ExpertStatus expertStatus, Double score, byte[] profilePictureData, Boolean isEmailVerified) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expertStatus = expertStatus;
        this.score = score;
        this.profilePictureData = profilePictureData;
        this.isEmailVerified = isEmailVerified;
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

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
