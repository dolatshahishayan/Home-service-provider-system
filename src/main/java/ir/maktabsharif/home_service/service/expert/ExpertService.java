package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Expert;

public interface ExpertService extends BaseService<Expert, ExpertSaveUpdateRequest> {
    void updateStatusToVerified(Integer expertId);
    void updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest);
    void register(ExpertSaveUpdateRequest expertSaveUpdateRequest,String imagePath);
}
