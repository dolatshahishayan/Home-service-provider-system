package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ExpertServiceService extends BaseService<ExpertService,Integer> {
    ExpertService findByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    void addExpertToService(Integer expertId, Integer serviceId);
    void removeExpertFromService(Integer expertId, Integer serviceId);
    List<ExpertService> findByExpertId(Integer expertId);
    List<Integer> findExpertIdsByServiceIds(List<Integer> serviceIds);
}
