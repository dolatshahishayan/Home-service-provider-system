package ir.maktabsharif.home_service.dto.suggestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;



public class SuggestionFindResponse {
    private Integer id;
    private Integer expertId;
    private Integer orderId;
    private LocalDateTime creationDate;
    private String description;
    private Double price;
    private Double workDuration;
    private LocalDateTime startDate;
    private Boolean accepted;

    public SuggestionFindResponse() {
    }

    public SuggestionFindResponse(Integer id, Integer expertId, Integer orderId, LocalDateTime creationDate, String description, Double price, Double workDuration, LocalDateTime startDate, Boolean accepted) {
        this.id = id;
        this.expertId = expertId;
        this.orderId = orderId;
        this.creationDate = creationDate;
        this.description = description;
        this.price = price;
        this.workDuration = workDuration;
        this.startDate = startDate;
        this.accepted = accepted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExpertId() {
        return expertId;
    }

    public void setExpertId(Integer expertId) {
        this.expertId = expertId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(Double workDuration) {
        this.workDuration = workDuration;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
