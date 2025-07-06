package ir.maktabsharif.home_service.controller.suggestion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suggestion")
@RequiredArgsConstructor
@Tag(name = "Suggestion controller",description = "Controller class for suggestion")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final SuggestionMapper suggestionMapper;

    @PostMapping("/save")
    @Operation(summary = "Save suggestion",description = "Method for saving a suggestion")
    public ResponseEntity<SuggestionFindResponse> save(@RequestBody @Validated(ValidationGroup.save.class) SuggestionSaveUpdateRequest suggestion) {
        Suggestion saved = suggestionService.registerSuggestionForOrder(suggestion);
        return ResponseEntity.ok(suggestionMapper.mapToResponse(saved));
    }

    @PutMapping("/update")
    @Operation(summary = "Update suggestion",description = "Method for updating a suggestion")
    public ResponseEntity<SuggestionFindResponse> update(@RequestBody @Validated(ValidationGroup.update.class) SuggestionSaveUpdateRequest suggestion) {
        Suggestion updated = suggestionService.updateWithDTO(suggestion);
        return ResponseEntity.ok(suggestionMapper.mapToResponse(updated));
    }

    @PutMapping("/confirm-suggestion-acceptance")
    @Operation(summary = "Confirm suggestion acceptance",description = "Method for confirming a suggestion's acceptance")
    public ResponseEntity<String> confirmSuggestionAcceptance(@RequestParam Integer suggestionId) {
        suggestionService.confirmSuggestionAcceptance(suggestionId);
        return ResponseEntity.ok("Suggestion's acceptance has been confirmed");
    }

    @GetMapping("/find-all-by-expert-id")
    @Operation(summary = "Find all by expert id",description = "Finds all suggestions by expert id")
    public ResponseEntity<List<SuggestionFindResponse>> findAllByExpertId(@RequestParam Integer expertId) {
        return ResponseEntity.ok(suggestionService.findAllByExpertId(expertId));
    }

    @GetMapping("find-all-and-sort-by-price-ascending")
    @Operation(summary = "Find all and sort by price ascending",description = "Finds all suggestions sorted by price ascending")
    public ResponseEntity<List<SuggestionFindResponse>> findAllAndSortByPriceAscending(@RequestParam Integer orderId) {
        List<Suggestion> allAndSortByPriceAsc = suggestionService.findAllAndSortByPriceAsc(orderId);
        List<SuggestionFindResponse> responses=new ArrayList<>();
        for (Suggestion suggestion : allAndSortByPriceAsc) {
            responses.add(suggestionMapper.mapToResponse(suggestion));
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/find-all-and-sort-by-expert-score-descending")
    @Operation(summary = "Find all and sort by expert score descending",description = "Finds all suggestions sorted by expert score descending")
    public ResponseEntity<List<SuggestionFindResponse>> findAllAndSortByExpertScoreDescending(@RequestParam Integer orderId) {
        List<Suggestion> allByAndSortByExpertScoreDesc = suggestionService.findAllByAndSortByExpertScoreDesc(orderId);
        List<SuggestionFindResponse> responses=new ArrayList<>();
        for (Suggestion suggestion : allByAndSortByExpertScoreDesc) {
            responses.add(suggestionMapper.mapToResponse(suggestion));
        }
        return ResponseEntity.ok(responses);
    }
 }
