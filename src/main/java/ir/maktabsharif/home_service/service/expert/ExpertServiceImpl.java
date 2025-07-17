package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.*;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.enums.Role;
import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert.ExpertRepository;
import ir.maktabsharif.home_service.service.email.EmailService;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.ImageUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ExpertServiceImpl extends BaseServiceImpl<Expert, Integer, ExpertRepository, ExpertMapper> implements ExpertService {
    protected final UserService userService;
    protected final WalletService walletService;
    protected final ImageUtil imageUtil;
    protected final OrderService orderService;
    protected final EmailService emailService;

    public ExpertServiceImpl(ExpertRepository repository, ExpertMapper expertMapper, UserService userService, WalletService walletService, ImageUtil imageUtil, @Lazy OrderService orderService, EmailService emailService) {
        super(repository, expertMapper);
        this.userService = userService;
        this.walletService = walletService;
        this.imageUtil = imageUtil;
        this.orderService = orderService;
        this.emailService = emailService;
    }

    @Override
    public void updateStatusToVerified(Integer expertId) {
        Expert byId = findById(expertId);
        byId.setExpertStatus(ExpertStatus.VERIFIED);
        save(byId);
    }

    @Override
    public void updateStatusToUnverified(Integer expertId) {
        Expert byId = findById(expertId);
        byId.setExpertStatus(ExpertStatus.DISABLED);
        save(byId);
    }

    @Override
    public Expert register(ExpertSaveUpdateRequest expertSaveUpdateRequest, String imagePath) {
        Expert expert = mapper.mapToEntity(expertSaveUpdateRequest);
        if (userService.existsByEmail(expertSaveUpdateRequest.getEmail().toLowerCase())) {
            throw new UserWithSameEmailExistsException();
        }
        byte[] bytesForExpert;
        if (imagePath != null) {
            bytesForExpert = imageUtil.getBytesForExpert(imagePath);
            if (!imagePath.endsWith(".jpg")) {
                throw new ImageFormatException("Image format should be jpg");
            }
            if (bytesForExpert.length > 300000) {
                throw new ImageLengthOutOfBoundException("Image size is more than 300kb.");
            }
            expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
            expert.setProfilePictureData(bytesForExpert);
            expert.setEnabled(false);
            return getExpert(expertSaveUpdateRequest, expert);
        }
        expert.setExpertStatus(ExpertStatus.NEW);
        expert.setEnabled(false);
        return getExpert(expertSaveUpdateRequest, expert);
    }

    private Expert getExpert(ExpertSaveUpdateRequest expertSaveUpdateRequest, Expert expert) {
        expert.setRole(Role.EXPERT);
        expert.setEmail(expertSaveUpdateRequest.getEmail().toLowerCase());
        expert.setRegistrationDate(LocalDateTime.now());
        save(expert);
        EmailVerificationToken token = emailService.createToken(expert, 60);
        String link = emailService.buildFrontendVerificationLink(token);
        emailService.sendVerificationEmail(expert.getEmail(), expert.getFirstName(), link);
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
    public List<Expert> findAll(Specification<Expert> spec) {
        return repository.findAll(spec);
    }

    @Override
    public Expert updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest,String imagePath) {
        Expert expert = findById(expertSaveUpdateRequest.getId());
        if (userService.existsByEmailAndIdNot(expertSaveUpdateRequest.getEmail(), expertSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        if (orderService.existsBySpecialistAndOrderStatusIn(expert.getId(), List.of(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, OrderStatus.STARTED))) {
            throw new ExpertHasAnActiveOrderException();
        }
        if (imagePath!=null) {
            byte[] bytesForExpert = imageUtil.getBytesForExpert(imagePath);
            if (!imagePath.endsWith(".jpg")) {
                throw new ImageFormatException("Image format should be jpg");
            }
            if (bytesForExpert.length > 300000) {
                throw new ImageLengthOutOfBoundException("Image size is more than 300kb.");
            }
            expert.setProfilePictureData(bytesForExpert);
        }
        mapper.updateEntityWithDTO(expertSaveUpdateRequest, expert);
        expert.setEmail(expertSaveUpdateRequest.getEmail().toLowerCase());
        expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
        return save(expert);
    }
}
