package ir.maktabsharif.home_service.service.expert;

import com.fasterxml.jackson.databind.ser.Serializers;
import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Expert;

public interface ExpertService extends BaseService<Expert, ExpertSaveUpdateRequest> {
    void approveExpert(Integer expertId);
    void register(ExpertSaveUpdateRequest expertSaveUpdateRequest);
}
