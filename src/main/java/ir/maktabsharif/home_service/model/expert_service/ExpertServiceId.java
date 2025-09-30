package ir.maktabsharif.home_service.model.expert_service;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable


public class ExpertServiceId implements Serializable {
    private Integer expertId;
    private Integer serviceId;

    public ExpertServiceId() {
    }

    public ExpertServiceId(Integer expertId, Integer serviceId) {
        this.expertId = expertId;
        this.serviceId = serviceId;
    }

    public Integer getExpertId() {
        return expertId;
    }

    public void setExpertId(Integer expertId) {
        this.expertId = expertId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
}
