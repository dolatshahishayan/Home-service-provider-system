package ir.maktabsharif.home_service.controller.suggestion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/suggestion")
@RequiredArgsConstructor
@Tag(name = "Suggestion controller",description = "Controller class for suggestion")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final SuggestionMapper suggestionMapper;

    @PostMapping("/save")
    @Operation(summary = "Save suggestion",description = "Method for saving a suggestion")
    public ResponseEntity<SuggestionFindResponse> save(@RequestBody SuggestionSaveUpdateRequest suggestion) {
        Suggestion saved = suggestionService.registerSuggestionForOrder(suggestion);
        return ResponseEntity.ok(suggestionMapper.mapToResponse(saved));
    }
}
