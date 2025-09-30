package ir.maktabsharif.home_service.dto.expert_service;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import javax.validation.constraints.*;


public class ExpertServiceSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private Integer expertId;
    @NotNull(groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private Integer serviceId;

    public ExpertServiceSaveUpdateRequest() {
    }

    public ExpertServiceSaveUpdateRequest(Integer expertId, Integer serviceId) {
        this.expertId = expertId;
        this.serviceId = serviceId;
    }

    public @NotNull(groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) Integer getExpertId() {
        return expertId;
    }

    public void setExpertId(@NotNull(groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) Integer expertId) {
        this.expertId = expertId;
    }

    public @NotNull(groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(@NotNull(groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) Integer serviceId) {
        this.serviceId = serviceId;
    }
}
