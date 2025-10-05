package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ExpertService extends BaseService<Expert,Integer> {
    void updateStatusToVerified(Integer expertId);
    Expert updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest);
    Expert register(ExpertSaveUpdateRequest expertSaveUpdateRequest);
    Expert findByEmail(String email);
    Page<Expert> findAll(Specification<Expert> spec, Pageable  pageable);
    void updateStatusToUnverified(Integer expertId);
}
