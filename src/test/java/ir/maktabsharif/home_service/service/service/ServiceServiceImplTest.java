package ir.maktabsharif.home_service.service.service;

import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.exception.DuplicateInfoException;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.repository.service.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ServiceServiceImpl service;


    @Test
    void updateWithDTO_shouldThrowException_whenNameExists() {
        ServiceSaveUpdateRequest dto = new ServiceSaveUpdateRequest();
        dto.setName("Cleaning");

        when(repository.existsByName("Cleaning")).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> service.updateWithDTO(dto));
        verify(mapper, never()).mapToEntity(any());
    }

    @Test
    void updateWithDTO_shouldUpdate_whenNameIsUnique() {
        ServiceSaveUpdateRequest dto = new ServiceSaveUpdateRequest();
        dto.setName("Painting");
        dto.setParentServiceId(1);

        Service mapped = new Service();
        Service parent = new Service();
        parent.setId(1);

        when(repository.existsByName("Painting")).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(mapped);
        when(repository.findById(1)).thenReturn(Optional.of(parent));

        service.updateWithDTO(dto);

        verify(mapper).mapToEntity(dto);
        verify(repository).update(mapped);
        assertEquals(parent, mapped.getParentService());
    }
    @Test
    void updateDescription_shouldUpdateDescription_whenIdExists() {
        Service serviceEntity = new Service();
        serviceEntity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(serviceEntity));

        service.updateDescription(10, "New description");

        assertEquals("New description", serviceEntity.getDescription());
        verify(repository).update(serviceEntity);
    }
    @Test
    void updateBasePrice_shouldUpdatePrice_whenIdExists() {
        Service serviceEntity = new Service();
        serviceEntity.setId(12);

        when(repository.findById(12)).thenReturn(Optional.of(serviceEntity));

        service.updateBasePrice(12, 500.0);

        assertEquals(500.0, serviceEntity.getBasePrice());
        verify(repository).update(serviceEntity);
    }
    @Test
    void existsByName_shouldCallRepository() {
        when(repository.existsByName("Plumbing")).thenReturn(true);
        boolean result = service.existsByName("Plumbing");
        assertTrue(result);
        verify(repository).existsByName("Plumbing");
    }
    @Test
    void saveWithDTO_shouldThrowException_whenNameExists() {
        ServiceSaveUpdateRequest dto = new ServiceSaveUpdateRequest();
        dto.setName("Cleaning");

        when(repository.existsByName("Cleaning")).thenReturn(true);

        assertThrows(DuplicateInfoException.class, () -> service.saveWithDTO(dto));
        verify(mapper, never()).mapToEntity(any());
        verify(repository, never()).save(any());
    }

    @Test
    void saveWithDTO_shouldSave_whenNameIsUnique() {
        ServiceSaveUpdateRequest dto = new ServiceSaveUpdateRequest();
        dto.setName("Painting");
        dto.setParentServiceId(1);

        Service mapped = new Service();
        Service parent = new Service();
        parent.setId(1);

        when(repository.existsByName("Painting")).thenReturn(false);
        when(mapper.mapToEntity(dto)).thenReturn(mapped);
        when(repository.findById(1)).thenReturn(Optional.of(parent));

        service.saveWithDTO(dto);

        verify(mapper).mapToEntity(dto);
        verify(repository).save(mapped);
        assertEquals(parent, mapped.getParentService());
    }


}

