package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.expert_service.Expert_Service;

import java.util.List;

public interface Expert_ServiceRepository extends CrudRepository<Expert_Service> {
    Expert_Service findByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    List<Expert_Service> findByExpertId(Integer expertId);
}
