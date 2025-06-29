package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.expert_service.Expert_ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;

import java.util.List;

public interface Expert_ServiceService extends BaseService<Expert_Service, Expert_ServiceSaveUpdateRequest> {
    Expert_Service findByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    void addExpertToService(Integer expertId, Integer serviceId);
    void removeExpertFromService(Integer expertId, Integer serviceId);
    List<Expert_Service> findByExpertId(Integer expertId);
}
