package ir.maktabsharif.home_service.dto.expert_service;

public class ExpertServiceFindResponse {
    private Integer expertId;
    private Integer serviceId;

    public ExpertServiceFindResponse() {
    }

    public ExpertServiceFindResponse(Integer expertId, Integer serviceId) {
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
