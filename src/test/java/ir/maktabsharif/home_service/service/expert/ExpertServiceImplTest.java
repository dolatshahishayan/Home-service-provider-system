package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.*;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert.ExpertRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.EmailUtil;
import ir.maktabsharif.home_service.util.ImageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpertServiceImplTest {

    @InjectMocks
    private ExpertServiceImpl expertService;

    @Mock
    private ExpertRepository repository;
    @Mock
    private ExpertMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;
    @Mock
    private ImageUtil imageUtil;
    @Mock
    private OrderService orderService;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void updateStatusToVerified_shouldSetStatusToVerified() {
        Expert expert = new Expert();
        expert.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(expert));
        when(repository.save(expert)).thenReturn(expert);

        expertService.updateStatusToVerified(1);

        assertEquals(ExpertStatus.VERIFIED, expert.getExpertStatus());
        verify(repository).save(expert);
    }

    @Test
    void updateStatusToUnverified_shouldSetStatusToDisabled() {
        Expert expert = new Expert();
        expert.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(expert));
        when(repository.save(expert)).thenReturn(expert);

        expertService.updateStatusToUnverified(1);

        assertEquals(ExpertStatus.DISABLED, expert.getExpertStatus());
    }

    @Test
    void register_shouldRegisterExpertWithImage() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("a@b.com");
        dto.setPassword("pass");

        Expert expert = new Expert();
        expert.setEmail("a@b.com");
        expert.setEmailVerified(false);
        byte[] imageData = new byte[100];
        EmailVerificationToken token = new EmailVerificationToken();

        when(imageUtil.getBytesForExpert("image.jpg")).thenReturn(imageData);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(repository.save(any())).thenReturn(expert);
        when(emailUtil.createToken(any(), anyInt())).thenReturn(token);
        when(repository.findByEmail(any())).thenReturn(Optional.of(expert));

        Expert result = expertService.register(dto, "image.jpg");

        assertNotNull(result);
        assertEquals(ExpertStatus.NEW, result.getExpertStatus());
        verify(walletService).saveWithDTO(any(WalletSaveUpdateRequest.class));
    }

    @Test
    void register_shouldThrowIfImageNotJpg() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("a@b.com");
        Expert expert = new Expert();
        expert.setEmailVerified(false);

        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(expert));
        when(imageUtil.getBytesForExpert("img.png")).thenReturn(new byte[100]);

        assertThrows(ImageFormatException.class, () ->
                expertService.register(dto, "img.png"));
    }

    @Test
    void register_shouldThrowIfImageTooLarge() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("a@b.com");
        Expert expert = new Expert();
        expert.setEmailVerified(false);
        byte[] bigImage = new byte[400000];
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(expert));
        when(imageUtil.getBytesForExpert("image.jpg")).thenReturn(bigImage);

        assertThrows(ImageLengthOutOfBoundException.class, () ->
                expertService.register(dto, "image.jpg"));
    }

    @Test
    void findByEmail_shouldReturnExpert() {
        Expert expert = new Expert();
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(expert));

        Expert result = expertService.findByEmail("a@b.com");

        assertEquals(expert, result);
    }

    @Test
    void findByEmail_shouldThrowIfNotFound() {
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        assertThrows(NoElementFoundException.class, () ->
                expertService.findByEmail("a@b.com"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void findAll_shouldCallRepository() {
        Specification<Expert> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Expert> page = mock(Page.class);
        when(repository.findAll(spec, pageable)).thenReturn(page);

        Page<Expert> result = expertService.findAll(spec, pageable);

        assertEquals(page, result);
    }

    @Test
    void updateWithDTO_shouldUpdateExpert() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("a@b.com");
        dto.setPassword("pass");

        Expert expert = new Expert();
        expert.setId(1);
        expert.setEmailVerified(true);

        when(repository.findById(1)).thenReturn(Optional.of(expert));
        when(repository.save(any())).thenReturn(expert);

        Expert result = expertService.updateWithDTO(dto, null);

        assertEquals("a@b.com", result.getEmail());
    }

    @Test
    void updateWithDTO_shouldThrowIfEmailExists() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("a@b.com");

        Expert expert = new Expert();
        expert.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(expert));
        when(userService.existsByEmailAndIdNot("a@b.com", 1)).thenReturn(true);

        assertThrows(UserWithSameEmailExistsException.class, () ->
                expertService.updateWithDTO(dto, null));
    }

    @Test
    void updateWithDTO_shouldThrowIfActiveOrderExists() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("a@b.com");

        Expert expert = new Expert();
        expert.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(expert));
        when(userService.existsByEmailAndIdNot("a@b.com", 1)).thenReturn(false);
        when(orderService.existsBySpecialistAndOrderStatusIn(eq(1), anyList())).thenReturn(true);

        assertThrows(ExpertHasAnActiveOrderException.class, () ->
                expertService.updateWithDTO(dto, null));
    }

    @Test
    void updateWithDTO_shouldUpdateImageToo() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setId(1);
        dto.setEmail("a@b.com");

        Expert expert = new Expert();
        expert.setId(1);
        expert.setEmailVerified(true);

        when(repository.findById(1)).thenReturn(Optional.of(expert));
        when(imageUtil.getBytesForExpert("image.jpg")).thenReturn(new byte[100]);
        when(repository.save(any())).thenReturn(expert);

        Expert result = expertService.updateWithDTO(dto, "image.jpg");

        assertNotNull(result);
        assertEquals("a@b.com", result.getEmail());
    }
}
