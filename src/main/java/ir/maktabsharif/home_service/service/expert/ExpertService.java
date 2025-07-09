package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Expert;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ExpertService extends BaseService<Expert,Integer> {
    void updateStatusToVerified(Integer expertId);
    Expert updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest,String imagePath);
    Expert register(ExpertSaveUpdateRequest expertSaveUpdateRequest,String imagePath);
    Expert findByEmail(String email);
    List<Expert> findAll(Specification<Expert> spec);
}
