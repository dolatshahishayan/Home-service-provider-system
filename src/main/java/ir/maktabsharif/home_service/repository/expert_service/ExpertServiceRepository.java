package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;

import java.util.List;

public interface ExpertServiceRepository extends CrudRepository<ExpertService> {
    ExpertService findByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    List<ExpertService> findByExpertId(Integer expertId);
}
