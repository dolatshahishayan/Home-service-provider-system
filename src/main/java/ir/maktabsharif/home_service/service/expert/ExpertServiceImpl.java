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
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.EmailUtil;
import ir.maktabsharif.home_service.util.ImageUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpertServiceImpl extends BaseServiceImpl<Expert, Integer, ExpertRepository, ExpertMapper> implements ExpertService {
    protected final UserService userService;
    protected final WalletService walletService;
    protected final ImageUtil imageUtil;
    protected final OrderService orderService;
    protected final EmailUtil emailUtil;
    protected final PasswordEncoder passwordEncoder;

    public ExpertServiceImpl(ExpertRepository repository, ExpertMapper expertMapper, UserService userService, WalletService walletService, ImageUtil imageUtil, @Lazy OrderService orderService, EmailUtil emailUtil, PasswordEncoder passwordEncoder) {
        super(repository, expertMapper);
        this.userService = userService;
        this.walletService = walletService;
        this.imageUtil = imageUtil;
        this.orderService = orderService;
        this.emailUtil = emailUtil;
        this.passwordEncoder = passwordEncoder;
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
        Expert expert;
        Optional<Expert> byEmail=repository.findByEmail(expertSaveUpdateRequest.getEmail());
        if (byEmail.isPresent()) {
            expert=byEmail.get();
            if (expert.getIsEmailVerified()){
                throw new UserWithSameEmailExistsException();
            }
        }else {
            expert = mapper.mapToEntity(expertSaveUpdateRequest);
        }
        expert.setExpertStatus(ExpertStatus.NEW);
        expert.setIsEmailVerified(false);
        if (imagePath != null) {
            return getExpertWithPicture(expertSaveUpdateRequest, imagePath, expert);
        }
        return getExpert(expertSaveUpdateRequest, expert);
    }

    private Expert getExpertWithPicture(ExpertSaveUpdateRequest expertSaveUpdateRequest, String imagePath, Expert expert) {
        byte[] bytesForExpert;
        bytesForExpert = imageUtil.getBytesForExpert(imagePath);
        if (!imagePath.endsWith(".jpg")) {
            throw new ImageFormatException("Image format should be jpg");
        }
        if (bytesForExpert.length > 300000) {
            throw new ImageLengthOutOfBoundException("Image size is more than 300kb.");
        }
        expert.setProfilePictureData(bytesForExpert);
        return getExpert(expertSaveUpdateRequest, expert);
    }

    private Expert getExpert(ExpertSaveUpdateRequest expertSaveUpdateRequest, Expert expert) {
        expert.setRole(Role.ROLE_EXPERT);
        expert.setEmail(expertSaveUpdateRequest.getEmail().toLowerCase());
        expert.setPassword(passwordEncoder.encode(expertSaveUpdateRequest.getPassword()));
        expert.setRegistrationDate(LocalDateTime.now());
        save(expert);
        sendVerificationEmail(expert);
        Expert byEmail = findByEmail(expert.getEmail());
        createWalletForExpert(byEmail);
        return expert;
    }

    private void createWalletForExpert(Expert byEmail) {
        WalletSaveUpdateRequest walletSaveUpdateRequest = new WalletSaveUpdateRequest();
        walletSaveUpdateRequest.setUserId(byEmail.getId());
        walletService.saveWithDTO(walletSaveUpdateRequest);
    }

    private void sendVerificationEmail(Expert expert) {
        EmailVerificationToken token = emailUtil.createToken(expert, 60);
        String link = emailUtil.buildFrontendVerificationLink(token);
        emailUtil.sendVerificationEmail(expert.getEmail(), expert.getFirstName(), link);
    }

    @Override
    public Expert findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(NoElementFoundException::new);

    }

    @Override
    public Page<Expert> findAll(Specification<Expert> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public Expert updateWithDTO(ExpertSaveUpdateRequest expertSaveUpdateRequest, String imagePath) {
        Expert expert = findById(expertSaveUpdateRequest.getId());
        checkEmailAndOrderStatus(expertSaveUpdateRequest, expert);
        if (imagePath != null) {
            getExpertAndCheckEmail(imagePath, expert);
        }
        mapper.updateEntityWithDTO(expertSaveUpdateRequest, expert);
        if (expert.getPassword() != null) {
            expert.setPassword(passwordEncoder.encode(expertSaveUpdateRequest.getPassword()));
        }
        expert.setEmail(expertSaveUpdateRequest.getEmail().toLowerCase());
        return save(expert);
    }

    private void checkEmailAndOrderStatus(ExpertSaveUpdateRequest expertSaveUpdateRequest, Expert expert) {
        if (userService.existsByEmailAndIdNot(expertSaveUpdateRequest.getEmail(), expertSaveUpdateRequest.getId())) {
            throw new UserWithSameEmailExistsException();
        }
        if (orderService.existsBySpecialistAndOrderStatusIn(expert.getId(), List.of(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, OrderStatus.STARTED))) {
            throw new ExpertHasAnActiveOrderException();
        }
    }

    private void getExpertAndCheckEmail(String imagePath, Expert expert) {
        byte[] bytesForExpert = imageUtil.getBytesForExpert(imagePath);
        if (!imagePath.endsWith(".jpg")) {
            throw new ImageFormatException("Image format should be jpg");
        }
        if (bytesForExpert.length > 300000) {
            throw new ImageLengthOutOfBoundException("Image size is more than 300kb.");
        }
        expert.setProfilePictureData(bytesForExpert);
        if (expert.getIsEmailVerified()) {
            expert.setExpertStatus(ExpertStatus.WAITING_FOR_VERIFYING);
        }
    }
}
