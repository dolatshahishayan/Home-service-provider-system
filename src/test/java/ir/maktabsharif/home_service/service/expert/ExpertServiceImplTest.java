package ir.maktabsharif.home_service.service.expert;

import ir.maktabsharif.home_service.dto.expert.ExpertSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.wallet.WalletSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.ExpertHasAnActiveOrderException;
import ir.maktabsharif.home_service.exception.ImageFormatException;
import ir.maktabsharif.home_service.exception.ImageLengthOutOfBoundException;
import ir.maktabsharif.home_service.exception.UserWithSameEmailExistsException;
import ir.maktabsharif.home_service.mapper.expert.ExpertMapper;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.repository.expert.ExpertRepository;
import ir.maktabsharif.home_service.service.order.OrderService;
import ir.maktabsharif.home_service.service.user.UserService;
import ir.maktabsharif.home_service.service.wallet.WalletService;
import ir.maktabsharif.home_service.util.ImageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpertServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ExpertRepository expertRepository;

    @Mock
    private ExpertMapper mapper;

    @Mock
    private WalletService walletService;

    @Mock
    private OrderService orderService;

    @Mock
    private ImageUtil imageUtil;

    @InjectMocks
    private ExpertServiceImpl expertService;


    @Test
    void updateStatusToVerified_ShouldSetStatusVerified() {
        Integer expertId = 1;
        Expert expert = new Expert();
        expert.setExpertStatus(ExpertStatus.NEW);

        when(expertRepository.findById(expertId)).thenReturn(Optional.of(expert));

        expertService.updateStatusToVerified(expertId);

        assertEquals(ExpertStatus.VERIFIED, expert.getExpertStatus());

        verify(expertRepository).save(expert);
    }


    @Test
    void register_ShouldThrow_WhenEmailExists() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("test@example.com");
        String imagePath = "profile.jpg";
        when(mapper.mapToEntity(any())).thenReturn(new Expert());

        when(userService.existsByEmail(dto.getEmail())).thenReturn(true);

        UserWithSameEmailExistsException ex = assertThrows(UserWithSameEmailExistsException.class, () -> expertService.register(dto, imagePath));

        assertNotNull(ex);
    }

    @Test
    void register_ShouldThrow_WhenImageFormatNotJpg() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("test@example.com");
        String imagePath = "profile.png";

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);

        ImageFormatException ex = assertThrows(ImageFormatException.class, () -> expertService.register(dto, imagePath));

        assertEquals("Image format should be jpg", ex.getMessage());
    }

    @Test
    void register_ShouldThrow_WhenImageSizeTooLarge() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("test@example.com");
        String imagePath = "profile.jpg";

        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);

        Expert expert = new Expert();
        when(mapper.mapToEntity(dto)).thenReturn(expert);

        when(imageUtil.getBytesForExpert(imagePath)).thenReturn(new byte[400_000]);

        ImageLengthOutOfBoundException ex = assertThrows(ImageLengthOutOfBoundException.class, () -> expertService.register(dto, imagePath));

        assertEquals("Image size is more than 300kb.", ex.getMessage());
    }


    @Test
    void register_ShouldSaveExpertAndCreateWallet_WhenValid() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("test@example.com");
        String imagePath = "profile.jpg";
        WalletSaveUpdateRequest walletSaveUpdateRequest= new WalletSaveUpdateRequest();
        Expert expert = new Expert();
        expert.setEmail(dto.getEmail());
        expert.setId(1);
        walletSaveUpdateRequest.setUserId(expert.getId());
        when(userService.existsByEmail(dto.getEmail())).thenReturn(false);
        when(imageUtil.getBytesForExpert(imagePath)).thenReturn(new byte[100_000]);
        when(mapper.mapToEntity(dto)).thenReturn(expert);
        when(expertRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(expert));
        expertService.register(dto, imagePath);

        assertEquals(ExpertStatus.NEW, expert.getExpertStatus());
        assertNotNull(expert.getRegistrationDate());

        verify(expertRepository).save(expert);

        verify(walletService).saveWithDTO(any(WalletSaveUpdateRequest.class));
    }

    @Test
    void updateWithDTO_ShouldThrow_WhenEmailUsedByAnother() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setId(42);
        dto.setEmail("existing@example.com");
        dto.setPassword("newpass");

        Expert existingExpert = new Expert();
        existingExpert.setId(42);

        when(expertRepository.findById(dto.getId())).thenReturn(Optional.of(existingExpert));

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(true);

        UserWithSameEmailExistsException exception = assertThrows(UserWithSameEmailExistsException.class,
                () -> expertService.updateWithDTO(dto));

        assertNotNull(exception);

        verify(orderService, never()).existsBySpecialistAndOrderStatusIn(any(), any());

        verify(expertRepository, never()).save(any());
    }


    @Test
    void updateWithDTO_ShouldThrow_WhenExpertHasActiveOrder() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("test@example.com");
        dto.setId(1);

        Expert expert = new Expert();
        expert.setId(1);

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(false);
        when(expertRepository.findById(dto.getId())).thenReturn(Optional.of(expert));
        when(orderService.existsBySpecialistAndOrderStatusIn(
                eq(expert),
                eq(List.of(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, OrderStatus.STARTED))
        )).thenReturn(true);

        ExpertHasAnActiveOrderException ex = assertThrows(ExpertHasAnActiveOrderException.class, () -> expertService.updateWithDTO(dto));

        assertNotNull(ex);
    }

    @Test
    void updateWithDTO_ShouldUpdateExpert_WhenValid() {
        ExpertSaveUpdateRequest dto = new ExpertSaveUpdateRequest();
        dto.setEmail("test@example.com");
        dto.setPassword("pass");
        dto.setId(1);

        Expert expert = new Expert();
        expert.setId(1);

        when(userService.existsByEmailAndIdNot(dto.getEmail(), dto.getId())).thenReturn(false);
        when(expertRepository.findById(dto.getId())).thenReturn(Optional.of(expert));
        when(orderService.existsBySpecialistAndOrderStatusIn(
                eq(expert),
                eq(List.of(OrderStatus.WAITING_FOR_EXPERT_TO_VISIT, OrderStatus.STARTED))
        )).thenReturn(false);

        expertService.updateWithDTO(dto);

        assertEquals(dto.getEmail(), expert.getEmail());
        assertEquals(dto.getPassword(), expert.getPassword());
        assertEquals(ExpertStatus.WAITING_FOR_VERIFYING, expert.getExpertStatus());

        verify(expertRepository).save(expert);
    }

    @Test
    void findByEmail_ShouldFindExpert_WhenEmailExists() {
        Expert expert = new Expert();
        when(expertRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(expert));
        expertService.findByEmail("exists@example.com");
        verify(expertRepository).findByEmail("exists@example.com");
    }
}
