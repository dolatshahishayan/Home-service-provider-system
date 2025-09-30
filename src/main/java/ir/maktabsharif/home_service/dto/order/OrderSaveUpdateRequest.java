package ir.maktabsharif.home_service.dto.order;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
public class OrderSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String description;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double proposedPrice;
    @FutureOrPresent
    private LocalDateTime startDate;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String address;
    @NotNull(groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private Integer serviceId;
    private Integer expertId;
    private Double finalPrice;

    public @NotNull(groups = {ValidationGroup.Update.class}) Integer getId() {
        return id;
    }

    public void setId(@NotNull(groups = {ValidationGroup.Update.class}) Integer id) {
        this.id = id;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(groups = {ValidationGroup.Save.class}) String description) {
        this.description = description;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Double getProposedPrice() {
        return proposedPrice;
    }

    public void setProposedPrice(@NotNull(groups = {ValidationGroup.Save.class}) Double proposedPrice) {
        this.proposedPrice = proposedPrice;
    }

    public @FutureOrPresent LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(@FutureOrPresent LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(groups = {ValidationGroup.Save.class}) String address) {
        this.address = address;
    }

    public @NotNull(groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(@NotNull(groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getExpertId() {
        return expertId;
    }

    public void setExpertId(Integer expertId) {
        this.expertId = expertId;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
