package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.exception.NoElementFoundException;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.service.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ServiceServiceImplTest {

    @Mock
    private ServiceRepository repository;

    @Mock
    private ServiceMapper mapper;

    @InjectMocks
    private ServiceServiceImpl serviceService;

    @Test
    void saveWithDTO_shouldSaveSuccessfully() {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest();
        request.setName("Test");
        request.setParentServiceId(null);
        Service service = new Service();
        service.setName("test");

        when(repository.existsByName("Test")).thenReturn(false);
        when(mapper.mapToEntity(request)).thenReturn(service);
        when(repository.save(service)).thenReturn(service);

        Service result = serviceService.saveWithDTO(request);

        assertNotNull(result);
        assertEquals("test", result.getName());
        verify(repository).save(service);
    }

    @Test
    void saveWithDTO_shouldThrowExceptionWhenDuplicate() {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest();
        request.setName("Test");

        when(repository.existsByName("Test")).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> serviceService.saveWithDTO(request));
    }

    @Test
    void updateWithDTO_shouldUpdateSuccessfully() {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest();
        request.setId(1);
        request.setName("Updated");
        request.setParentServiceId(null);
        Service existing = new Service();
        existing.setId(1);
        Service updated = new Service();
        updated.setName("updated");

        when(repository.existsByName("Updated")).thenReturn(false);
        when(repository.findById(1)).thenReturn(Optional.of(existing));
        doAnswer(invocation -> {
            ServiceSaveUpdateRequest dto = invocation.getArgument(0);
            Service svc = invocation.getArgument(1);
            svc.setName(dto.getName());
            return null;
        }).when(mapper).updateEntityWithDTO(any(), any());
        when(repository.save(existing)).thenReturn(existing);

        Service result = serviceService.updateWithDTO(request);

        assertEquals("updated", result.getName());
        verify(repository).save(existing);
    }

    @Test
    void updateWithDTO_shouldThrowExceptionWhenDuplicate() {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest();
        request.setName("Duplicate");

        when(repository.existsByName("Duplicate")).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> serviceService.updateWithDTO(request));
    }

    @Test
    void updateDescription_shouldUpdateSuccessfully() {
        Service service = new Service();
        service.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(service));
        when(repository.save(service)).thenReturn(service);

        serviceService.updateDescription(1, "New Description");

        assertEquals("New Description", service.getDescription());
        verify(repository).save(service);
    }

    @Test
    void updateBasePrice_shouldUpdateSuccessfully() {
        Service service = new Service();
        service.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(service));
        when(repository.save(service)).thenReturn(service);

        serviceService.updateBasePrice(1, 123.45);

        assertEquals(123.45, service.getBasePrice());
        verify(repository).save(service);
    }

    @Test
    void findAllAndParentServiceIsNull_shouldReturnPage() {
        Page<Service> page = new PageImpl<>(Collections.singletonList(new Service()));
        when(repository.findByParentServiceIsNull(any())).thenReturn(page);

        Page<Service> result = serviceService.findAllAndParentServiceIsNull(PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllAndParentServiceIsNull_shouldThrowWhenEmpty() {
        when(repository.findByParentServiceIsNull(any())).thenReturn(Page.empty());

        assertThrows(NoElementFoundException.class, () -> serviceService.findAllAndParentServiceIsNull(PageRequest.of(0, 10)));
    }

    @Test
    void findAllAndParentServiceIsNotNullByParentService_shouldReturnPage() {
        Service parent = new Service();
        Page<Service> page = new PageImpl<>(Collections.singletonList(new Service()));
        when(repository.findByParentService(parent, PageRequest.of(0, 10))).thenReturn(page);

        Page<Service> result = serviceService.findAllAndParentServiceIsNotNullByParentService(parent, PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
    }

    @Test
    void findAllAndParentServiceIsNotNullByParentService_shouldThrowWhenEmpty() {
        Service parent = new Service();
        when(repository.findByParentService(parent, PageRequest.of(0, 10))).thenReturn(Page.empty());

        assertThrows(NoElementFoundException.class, () -> serviceService.findAllAndParentServiceIsNotNullByParentService(parent, PageRequest.of(0, 10)));
    }

    @Test
    void existsByName_shouldReturnTrue() {
        when(repository.existsByName("test")).thenReturn(true);
        assertTrue(serviceService.existsByName("test"));
    }

    @Test
    void existsByName_shouldReturnFalse() {
        when(repository.existsByName("notExist")).thenReturn(false);
        assertFalse(serviceService.existsByName("notExist"));
    }
}
