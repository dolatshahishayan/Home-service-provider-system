package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.ImageFormatException;
import ir.maktabsharif.home_service.exception.ImageLengthOutOfBoundException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert.ExpertRepository;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.ImageUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExpertServiceImpl extends BaseServiceImpl<Expert, ExpertSaveUpdateRequest, ExpertRepository, ExpertMapper> implements ExpertService {
    protected final UserService userService;
    protected final WalletService walletService;
    protected final ImageUtil imageUtil;

    public ExpertServiceImpl(ExpertRepository repository, ExpertMapper mapper, UserService userService, WalletService walletService, ImageUtil imageUtil) {
        super(repository, mapper);
        this.userService = userService;
        this.walletService = walletService;
        this.imageUtil = imageUtil;
    }

    @Override
    public void updateStatusToVerified(Integer expertId) {
        Expert byId = findById(expertId);
        byId.setExpertStatus(ExpertStatus.VERIFIED);
        update(byId);
    }

    @Override
    public void register(ExpertSaveUpdateRequest expertSaveUpdateRequest, String imagePath) {
        if (userService.existsByEmail(expertSaveUpdateRequest.getEmail())) {
            throw new UserWithSameEmailExistsException();
        }
        if (!imagePath.endsWith(".jpg")) {
            throw new ImageFormatException("Image format should be jpg");
        }
        Expert expert = mapper.mapToEntity(expertSaveUpdateRequest);
        expert.setProfilePictureData(imageUtil.getBytesForExpert(imagePath));
        if (expert.getProfilePictureData().length > 300000) {
            throw new ImageLengthOutOfBoundException("Image size is more than 300kb.");
        }
        expert.setExpertStatus(ExpertStatus.NEW);
        expert.setRegistrationDate(LocalDateTime.now());
        save(expert);

        walletService.saveWithExpert(expert);
    }

    @Override
    public void updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest) {
        if (userService.existsByEmailAndIdNot(expertSaveUpdateRequest.getEmail(), expertSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        Expert expert = findById(expertSaveUpdateRequest.getId());
        expert.setEmail(expertSaveUpdateRequest.getEmail());
        expert.setPassword(expertSaveUpdateRequest.getPassword());
//Todo add existsBySpecialistAndOrderStatusIn if
        expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
        update(expert);
    }
}
