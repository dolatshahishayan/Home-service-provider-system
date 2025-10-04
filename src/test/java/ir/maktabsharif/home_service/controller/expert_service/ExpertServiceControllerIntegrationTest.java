package ir.maktabsharif.home_service.controller.expert_service;

import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.expert_service.ExpertServiceFindResponse;
import ir.maktabsharif.home_service.mapper.expert_service.ExpertServiceMapper;
import ir.maktabsharif.home_service.model.expert_service.ExpertService;
import ir.maktabsharif.home_service.model.expert_service.ExpertServiceId;
import ir.maktabsharif.home_service.model.service.Service;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.service.expert_service.ExpertServiceService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
class ExpertServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExpertServiceService expertServiceService;
    @Autowired
    private ExpertServiceMapper expertServiceMapper;



    private ExpertService expertServiceEntity;
    private ExpertServiceFindResponse expertServiceFindResponse;

    @BeforeEach
    void setup() {
        Expert expert = new Expert();
        expert.setId(1);

        Service service = new Service();
        service.setId(2);

        ExpertServiceId id = new ExpertServiceId(1, 2);

        expertServiceEntity = new ExpertService();
        expertServiceEntity.setId(id);
        expertServiceEntity.setExpert(expert);
        expertServiceEntity.setService(service);

        expertServiceFindResponse = new ExpertServiceFindResponse(1, 2);
    }

    @Test
    void addExpertToService_ShouldReturnConfirmationMessage() throws Exception {
        Mockito.doNothing().when(expertServiceService).addExpertToService(1, 2);

        mockMvc.perform(post("/api/v1/expert-services/add-expert-to-service")
                        .param("expertId", "1")
                        .param("serviceId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("expert added to service"));
    }

    @Test
    void removeExpertFromService_ShouldReturnConfirmationMessage() throws Exception {
        Mockito.doNothing().when(expertServiceService).removeExpertFromService(1, 2);

        mockMvc.perform(delete("/api/v1/expert-services/remove-expert-from-service")
                        .param("expertId", "1")
                        .param("serviceId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("expert removed from service"));
    }

    @Test
    void findByExpertIdAndServiceId_ShouldReturnExpertService() throws Exception {
        Mockito.when(expertServiceService.findByExpertIdAndServiceId(1, 2)).thenReturn(expertServiceEntity);
        Mockito.when(expertServiceMapper.mapToResponse(any(ExpertService.class))).thenReturn(expertServiceFindResponse);

        mockMvc.perform(get("/api/v1/expert-services/find-by-expert-id-and-service-id")
                        .param("expertId", "1")
                        .param("serviceId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expertId").value(1))
                .andExpect(jsonPath("$.serviceId").value(2));
    }

    @Test
    void existsByServiceIdAndExpertId_ShouldReturnBoolean() throws Exception {
        Mockito.when(expertServiceService.existsByExpertIdAndServiceId(1, 2)).thenReturn(true);

        mockMvc.perform(get("/api/v1/expert-services/exists-by-service-id-and-expert-id")
                        .param("serviceId", "2")
                        .param("expertId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void findByExpertId_ShouldReturnPagedExpertService() throws Exception {
        Page<ExpertService> page = new PageImpl<>(List.of(expertServiceEntity));
        Mockito.when(expertServiceService.findByExpertId(eq(1), any(PageRequest.class))).thenReturn(page);
        Mockito.when(expertServiceMapper.mapToResponse(any(ExpertService.class))).thenReturn(expertServiceFindResponse);

        mockMvc.perform(get("/api/v1/expert-services/find-by-expert-id")
                        .param("expertId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].expertId").value(1))
                .andExpect(jsonPath("$.content[0].serviceId").value(2));
    }
}
