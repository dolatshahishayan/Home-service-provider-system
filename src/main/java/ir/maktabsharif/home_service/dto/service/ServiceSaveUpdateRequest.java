package ir.maktabsharif.home_service.dto.service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import javax.validation.constraints.*;



public class ServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String name;
    @NotNull(groups = {ValidationGroup.Save.class})
    private Double basePrice;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String description;
    private Integer parentServiceId;

    public ServiceSaveUpdateRequest() {
    }

    public ServiceSaveUpdateRequest(Integer id, String name, Double basePrice, String description, Integer parentServiceId) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.parentServiceId = parentServiceId;
    }

    public @NotNull(groups = {ValidationGroup.Update.class}) Integer getId() {
        return id;
    }

    public void setId(@NotNull(groups = {ValidationGroup.Update.class}) Integer id) {
        this.id = id;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getName() {
        return name;
    }

    public void setName(@NotBlank(groups = {ValidationGroup.Save.class}) String name) {
        this.name = name;
    }

    public @NotNull(groups = {ValidationGroup.Save.class}) Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(@NotNull(groups = {ValidationGroup.Save.class}) Double basePrice) {
        this.basePrice = basePrice;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(groups = {ValidationGroup.Save.class}) String description) {
        this.description = description;
    }

    public Integer getParentServiceId() {
        return parentServiceId;
    }

    public void setParentServiceId(Integer parentServiceId) {
        this.parentServiceId = parentServiceId;
    }
}
