package ir.maktabsharif.home_service.controller.suggestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.maktabsharif.home_service.TestMockConfig;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.User;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestMockConfig.class)
class SuggestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SuggestionService suggestionService;
    @Autowired
    private SuggestionMapper suggestionMapper;
    @Autowired
    private SecurityContextUtil securityContextUtil;



    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);
        user.setEmail("expert@test.com");
        UserDetailsImpl principal = new UserDetailsImpl(user);

        Mockito.when(securityContextUtil.getCurrentUser()).thenReturn(principal);
    }

    @Test
    void saveSuggestion_ShouldReturnSavedSuggestion() throws Exception {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest(
                null, 1, "Test Description", 100.0, 2.0, LocalDateTime.now(), false
        );

        Suggestion saved = new Suggestion();
        saved.setId(1);
        saved.setDescription("Test Description");

        SuggestionFindResponse response = new SuggestionFindResponse(1, 1, 1,
                LocalDateTime.now(), "Test Description", 100.0, 2.0, LocalDateTime.now(), false);

        Mockito.when(suggestionService.registerSuggestionForOrder(any(), any())).thenReturn(saved);
        Mockito.when(suggestionMapper.mapToResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/suggestions/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void updateSuggestion_ShouldReturnUpdatedSuggestion() throws Exception {
        SuggestionSaveUpdateRequest request = new SuggestionSaveUpdateRequest(
                1, 1, "Updated Description", 150.0, 3.0, LocalDateTime.now(), true
        );

        Suggestion updated = new Suggestion();
        updated.setId(1);
        updated.setDescription("Updated Description");

        SuggestionFindResponse response = new SuggestionFindResponse(1, 1, 1,
                LocalDateTime.now(), "Updated Description", 150.0, 3.0, LocalDateTime.now(), true);

        Mockito.when(suggestionService.updateWithDTO(any(), eq(1))).thenReturn(updated);
        Mockito.when(suggestionMapper.mapToResponse(any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/suggestions/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void confirmSuggestionAcceptance_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(put("/api/v1/suggestions/confirm-suggestion-acceptance")
                        .param("suggestionId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Suggestion's acceptance has been confirmed"));

        Mockito.verify(suggestionService).confirmSuggestionAcceptance(1);
    }

    @Test
    void findAllByExpertId_ShouldReturnPageOfSuggestions() throws Exception {
        SuggestionFindResponse suggestionResponse = new SuggestionFindResponse(
                1, 1, 1, LocalDateTime.now(), "Test suggestion", 100.0, 2.0, LocalDateTime.now(), false
        );

        Mockito.when(suggestionService.findAllByExpertId(eq(1), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(suggestionResponse)));

        mockMvc.perform(get("/api/v1/suggestions/find-all-by-expert-id")
                        .param("expertId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].expertId").value(1))
                .andExpect(jsonPath("$.content[0].description").value("Test suggestion"))
                .andExpect(jsonPath("$.content[0].price").value(100.0))
                .andExpect(jsonPath("$.content[0].workDuration").value(2.0));
    }

    @Test
    void findAllAndSortByPriceAscending_ShouldReturnPagedSuggestions() throws Exception {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(1);
        SuggestionFindResponse response = new SuggestionFindResponse(1, 1, 1,
                LocalDateTime.now(), "Test", 100.0, 2.0, LocalDateTime.now(), false);

        Mockito.when(suggestionService.findAllAndSortByPriceAsc(eq(1), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(suggestion)));
        Mockito.when(suggestionMapper.mapToResponse(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/suggestions/find-all-and-sort-by-price-ascending")
                        .param("orderId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void findAllAndSortByExpertScoreDescending_ShouldReturnPagedSuggestions() throws Exception {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(1);
        SuggestionFindResponse response = new SuggestionFindResponse(1, 1, 1,
                LocalDateTime.now(), "Test", 100.0, 2.0, LocalDateTime.now(), false);

        Mockito.when(suggestionService.findAllByAndSortByExpertScoreDesc(eq(1), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(suggestion)));
        Mockito.when(suggestionMapper.mapToResponse(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/suggestions/find-all-and-sort-by-expert-score-descending")
                        .param("orderId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
}
