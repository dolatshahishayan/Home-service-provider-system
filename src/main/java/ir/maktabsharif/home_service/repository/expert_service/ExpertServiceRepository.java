package ir.maktabsharif.home_service.repository.expert_service;

import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ExpertServiceRepository extends JpaRepository<ExpertService,Integer>, JpaSpecificationExecutor<ExpertService> {
    Optional<ExpertService> findByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    boolean existsByExpertIdAndServiceId(Integer expertId, Integer serviceId);
    Optional<List<ExpertService>> findByExpertId(Integer expertId);
}
