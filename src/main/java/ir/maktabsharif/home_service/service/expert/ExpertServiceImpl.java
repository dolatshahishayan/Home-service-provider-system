package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.*;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert.ExpertRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.ImageUtil;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ExpertServiceImpl extends BaseServiceImpl<Expert, Integer, ExpertRepository, ExpertMapper> implements ExpertService {
    protected final UserService userService;
    protected final WalletService walletService;
    protected final ImageUtil imageUtil;
    protected final OrderService orderService;

    public ExpertServiceImpl(ExpertRepository repository, ExpertMapper expertMapper, UserService userService, WalletService walletService, ImageUtil imageUtil, @Lazy OrderService orderService) {
        super(repository, expertMapper);
        this.userService = userService;
        this.walletService = walletService;
        this.imageUtil = imageUtil;
        this.orderService = orderService;
    }

    @Override
    public void updateStatusToVerified(Integer expertId) {
        Expert byId = findById(expertId);
        byId.setExpertStatus(ExpertStatus.VERIFIED);
        save(byId);
    }

    @Override
    public Expert register(ExpertSaveUpdateRequest expertSaveUpdateRequest, String imagePath) {
        Expert expert = mapper.mapToEntity(expertSaveUpdateRequest);
        byte[] bytesForExpert = imageUtil.getBytesForExpert(imagePath);
        if (userService.existsByEmail(expertSaveUpdateRequest.getEmail().toLowerCase())) {
            throw new UserWithSameEmailExistsException();
        }
        if (!imagePath.endsWith(".jpg")) {
            throw new ImageFormatException("Image format should be jpg");
        }
        if (bytesForExpert.length > 300000) {
            throw new ImageLengthOutOfBoundException("Image size is more than 300kb.");
        }
        expert.setProfilePictureData(bytesForExpert);
        expert.setExpertStatus(ExpertStatus.NEW);
        expert.setRole(Role.EXPERT);
        expert.setEmail(expertSaveUpdateRequest.getEmail().toLowerCase());
        expert.setRegistrationDate(LocalDateTime.now());
        save(expert);
        Expert byEmail = findByEmail(expert.getEmail());
        WalletSaveUpdateRequest walletSaveUpdateRequest = new WalletSaveUpdateRequest();
        walletSaveUpdateRequest.setUserId(byEmail.getId());
        walletService.saveWithDTO(walletSaveUpdateRequest);
        return expert;
    }

    @Override
    public Expert findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(NoElementFoundException::new);

    }

    @Override
    public Expert updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest) {
        Expert expert = findById(expertSaveUpdateRequest.getId());
        if (userService.existsByEmailAndIdNot(expertSaveUpdateRequest.getEmail(), expertSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        if (orderService.existsBySpecialistAndOrderStatusIn(expert, List.of(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, OrderStatus.STARTED))) {
            throw new ExpertHasAnActiveOrderException();
        }
        mapper.updateEntityWithDTO(expertSaveUpdateRequest, expert);
        expert.setEmail(expertSaveUpdateRequest.getEmail().toLowerCase());
        expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
        return save(expert);
    }
}
