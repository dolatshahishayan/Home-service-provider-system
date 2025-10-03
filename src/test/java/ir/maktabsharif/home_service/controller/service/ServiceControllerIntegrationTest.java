package ir.maktabsharif.home_service.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.dto.service.ServiceFindResponse;
import ir.maktabsharif.home_service.dto.service.ServiceSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.service.ServiceMapper;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.service.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ServiceControllerIntegrationTest.MockConfig.class)
class ServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ServiceMapper serviceMapper;

    static class MockConfig {
        @Bean
        ServiceService serviceService() {
            return Mockito.mock(ServiceService.class);
        }

        @Bean
        ServiceMapper serviceMapper() {
            return Mockito.mock(ServiceMapper.class);
        }
    }

    private Service serviceEntity;
    private ServiceFindResponse serviceFindResponse;

    @BeforeEach
    void setup() {
        serviceEntity = new Service();
        serviceEntity.setId(1);
        serviceEntity.setName("Test Service");
        serviceEntity.setBasePrice(100.0);
        serviceEntity.setDescription("Test Description");

        serviceFindResponse = new ServiceFindResponse(1, "Test Service", 100.0, "Test Description", null);
    }

    @Test
    void saveService_ShouldReturnSavedService() throws Exception {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest();
        request.setName("Test Service");
        request.setDescription("Test Description");
        request.setBasePrice(100.0);

        Mockito.when(serviceService.saveWithDTO(any(ServiceSaveUpdateRequest.class))).thenReturn(serviceEntity);
        Mockito.when(serviceMapper.mapToResponse(any(Service.class))).thenReturn(serviceFindResponse);

        mockMvc.perform(post("/api/v1/services/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Service"))
                .andExpect(jsonPath("$.basePrice").value(100.0))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void updateService_ShouldReturnUpdatedService() throws Exception {
        ServiceSaveUpdateRequest request = new ServiceSaveUpdateRequest(1,"Updated Service", 150.0, "Updated Description", null);

        serviceEntity.setName("Updated Service");
        serviceEntity.setBasePrice(150.0);
        serviceEntity.setDescription("Updated Description");

        serviceFindResponse.setName("Updated Service");
        serviceFindResponse.setBasePrice(150.0);
        serviceFindResponse.setDescription("Updated Description");

        Mockito.when(serviceService.updateWithDTO(any(ServiceSaveUpdateRequest.class))).thenReturn(serviceEntity);
        Mockito.when(serviceMapper.mapToResponse(any(Service.class))).thenReturn(serviceFindResponse);

        mockMvc.perform(put("/api/v1/services/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Service"))
                .andExpect(jsonPath("$.basePrice").value(150.0))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void updateDescription_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(serviceService).updateDescription(1, "New Description");

        mockMvc.perform(put("/api/v1/services/update-description")
                        .param("serviceId", "1")
                        .param("description", "New Description"))
                .andExpect(status().isOk())
                .andExpect(content().string("Description updated"));
    }

    @Test
    void updateBasePrice_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(serviceService).updateBasePrice(1, 200.0);

        mockMvc.perform(put("/api/v1/services/update-base-price")
                        .param("serviceId", "1")
                        .param("basePrice", "200.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Base price updated"));
    }

    @Test
    void existsByName_ShouldReturnTrue() throws Exception {
        Mockito.when(serviceService.existsByName("Test Service")).thenReturn(true);

        mockMvc.perform(get("/api/v1/services/exists-by-name")
                        .param("name", "Test Service"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findAllParentServices_ShouldReturnPagedServices() throws Exception {
        Page<Service> page = new PageImpl<>(List.of(serviceEntity));

        Mockito.when(serviceService.findAllAndParentServiceIsNull(PageRequest.of(0, 10))).thenReturn(page);
        Mockito.when(serviceMapper.mapToResponse(any(Service.class))).thenReturn(serviceFindResponse);

        mockMvc.perform(get("/api/v1/services/find-all-parentServices")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Service"));
    }

    @Test
    void findAllSubServices_ReturnsPagedSubServices() throws Exception {
        int serviceId = 1;

        Service service = new Service();
        service.setId(serviceId);
        service.setName("Main Service");

        Service subService = new Service();
        subService.setId(2);
        subService.setName("Sub Service");
        subService.setParentService(service);

        ServiceFindResponse response = new ServiceFindResponse();
        response.setId(2);
        response.setName("Sub Service");

        Page<Service> subServicePage = new PageImpl<>(List.of(subService));

        Mockito.when(serviceService.findById(serviceId)).thenReturn(service);
        Mockito.when(serviceService.findAllAndParentServiceIsNotNullByParentService(Mockito.eq(service), Mockito.any(PageRequest.class)))
                .thenReturn(subServicePage);
        Mockito.when(serviceMapper.mapToResponse(Mockito.any(Service.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/services/find-all-subServices")
                        .param("serviceId", String.valueOf(serviceId))
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Sub Service"));
    }

    @Test
    void deleteService_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(serviceService).deleteById(1);

        mockMvc.perform(delete("/api/v1/services/delete")
                        .param("serviceId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted service"));
    }
}
