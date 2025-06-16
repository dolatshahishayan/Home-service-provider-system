package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.NoUserFoundWithGivenCredentialsException;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert.ExpertRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class ExpertServiceImpl extends BaseServiceImpl<Expert, ExpertSaveUpdateRequest, ExpertRepository, ExpertMapper> implements ExpertService {
    protected final UserService userService;
    public ExpertServiceImpl(ExpertRepository repository, ExpertMapper mapper, UserService userService) {
        super(repository, mapper);
        this.userService = userService;
    }

    @Override
    public void approveExpert(Integer expertId) {
        Expert byId = findById(expertId);
        byId.setExpertStatus(ExpertStatus.VERIFIED);
        update(byId);
    }

    @Override
    public void register(ExpertSaveUpdateRequest expertSaveUpdateRequest) {
        Expert expert = mapper.mapToEntity(expertSaveUpdateRequest);
        expert.setExpertStatus(ExpertStatus.UNVERIFIED);
        expert.setRegistrationDate(LocalDateTime.now());
        save(expert);
    }

    public void updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest) {
        update(findById(expertSaveUpdateRequest.getId()));
    }
}
