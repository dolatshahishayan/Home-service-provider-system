package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExpertServiceService extends BaseService<ExpertService,Integer> {
    ExpertService findByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    void addExpertToService(Integer expertId, Integer serviceId);
    void removeExpertFromService(Integer expertId, Integer serviceId);
    Page<ExpertService> findByExpertId(Integer expertId, Pageable  pageable);
    List<Integer> findExpertIdsByServiceIds(List<Integer> serviceIds);
    void deleteAll();
}
