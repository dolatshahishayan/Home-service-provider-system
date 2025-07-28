package ir.maktabsharif.home_service.service.expert_service;

import ir.maktabsharif.home_service.exception.ExpertAlreadyInServiceException;
import ir.maktabsharif.home_service.exception.NoExpertFoundWithServiceException;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.expert_service.ExpertServiceCriteriaRepository;
import ir.maktabsharif.home_service.repository.expert_service.ExpertServiceRepository;
import ir.maktabsharif.home_service.service.service.ServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ExpertServiceServiceImplTest {

    @InjectMocks
    private ExpertServiceServiceImpl expertServiceService;

    @Mock
    private ir.maktabsharif.home_service.service.expert.ExpertService expertService;
    @Mock
    private ExpertServiceRepository expertServiceRepository;

    @Mock
    private ExpertServiceCriteriaRepository expertServiceCriteriaRepository;

    @Mock
    private ServiceService serviceService;


    @Test
    void addExpertToService_shouldAddSuccessfully() {
        Service service = new Service();

        when(expertServiceRepository.existsByExpertIdAndServiceId(1, 2)).thenReturn(false);
        when(serviceService.findById(2)).thenReturn(service);

        expertServiceService.addExpertToService(1, 2);

        verify(expertServiceRepository).save(any(ExpertService.class));
    }

    @Test
    void addExpertToService_shouldThrowWhenAlreadyExists() {
        when(expertServiceRepository.existsByExpertIdAndServiceId(1, 2)).thenReturn(true);

        assertThrows(ExpertAlreadyInServiceException.class, () ->
                expertServiceService.addExpertToService(1, 2));
    }

    @Test
    void removeExpertFromService_shouldDeleteSuccessfully() {
        ExpertService entity = new ExpertService();
        when(expertServiceRepository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.of(entity));

        expertServiceService.removeExpertFromService(1, 2);

        verify(expertServiceRepository).delete(entity);
    }

    @Test
    void removeExpertFromService_shouldThrowWhenNotFound() {
        when(expertServiceRepository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.empty());

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                expertServiceService.removeExpertFromService(1, 2));
    }

    @Test
    void findByExpertId_shouldReturnPage() {
        ExpertService entity = new ExpertService();
        Page<ExpertService> page = new PageImpl<>(List.of(entity));
        Pageable pageable = PageRequest.of(0, 10);

        when(expertServiceRepository.findByExpertId(1, pageable)).thenReturn(page);

        Page<ExpertService> result = expertServiceService.findByExpertId(1, pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void findByExpertId_shouldThrowIfEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ExpertService> emptyPage = new PageImpl<>(List.of());

        when(expertServiceRepository.findByExpertId(1, pageable)).thenReturn(emptyPage);

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                expertServiceService.findByExpertId(1, pageable));
    }

    @Test
    void findExpertIdsByServiceIds_shouldReturnIds() {
        List<Integer> ids = List.of(1, 2, 3);
        when(expertServiceCriteriaRepository.findExpertIdsByServiceIds(ids)).thenReturn(ids);

        List<Integer> result = expertServiceService.findExpertIdsByServiceIds(ids);

        assertEquals(ids, result);
    }

    @Test
    void findByExpertIdAndServiceId_shouldReturnEntity() {
        ExpertService entity = new ExpertService();
        when(expertServiceRepository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.of(entity));

        ExpertService result = expertServiceService.findByExpertIdAndServiceId(1, 2);

        assertEquals(entity, result);
    }

    @Test
    void findByExpertIdAndServiceId_shouldThrowIfNotFound() {
        when(expertServiceRepository.findByExpertIdAndServiceId(1, 2)).thenReturn(Optional.empty());

        assertThrows(NoExpertFoundWithServiceException.class, () ->
                expertServiceService.findByExpertIdAndServiceId(1, 2));
    }

    @Test
    void existsByExpertIdAndServiceId_shouldReturnTrueOrFalse() {
        when(expertServiceRepository.existsByExpertIdAndServiceId(1, 2)).thenReturn(true);

        assertTrue(expertServiceService.existsByExpertIdAndServiceId(1, 2));

        when(expertServiceRepository.existsByExpertIdAndServiceId(1, 2)).thenReturn(false);

        assertFalse(expertServiceService.existsByExpertIdAndServiceId(1, 2));
    }
}
