package ir.maktabsharif.home_service.dto.suggestion;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


public class SuggestionSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Integer orderId;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String description;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double price;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double workDuration;
    @NotNull(groups = {ValidationGroup.Save.class})
    private LocalDateTime startDate;
    private Boolean accepted;

    public SuggestionSaveUpdateRequest() {
    }

    public SuggestionSaveUpdateRequest(Integer id, Integer orderId, String description, Double price, Double workDuration, LocalDateTime startDate, Boolean accepted) {
        this.id = id;
        this.orderId = orderId;
        this.description = description;
        this.price = price;
        this.workDuration = workDuration;
        this.startDate = startDate;
        this.accepted = accepted;
    }

    public @NotNull(groups = {ValidationGroup.Update.class}) Integer getId() {
        return id;
    }

    public void setId(@NotNull(groups = {ValidationGroup.Update.class}) Integer id) {
        this.id = id;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(@NotNull(groups = {ValidationGroup.Save.class}) Integer orderId) {
        this.orderId = orderId;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(groups = {ValidationGroup.Save.class}) String description) {
        this.description = description;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull(groups = {ValidationGroup.Save.class}) Double price) {
        this.price = price;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Double getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(@NotNull(groups = {ValidationGroup.Save.class}) Double workDuration) {
        this.workDuration = workDuration;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(groups = {ValidationGroup.Save.class}) LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
