package ir.maktabsharif.home_service.controller.suggestion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionFindResponse;
import ir.maktabsharif.home_service.dto.suggestion.SuggestionSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.suggestion.SuggestionMapper;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.suggestion.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suggestions")
@RequiredArgsConstructor
@Tag(name = "Suggestions controller", description = "Controller class for suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final SuggestionMapper suggestionMapper;
    private final SecurityContextUtil securityContextUtil;

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PostMapping("/save")
    @Operation(summary = "Save suggestion", description = "Method for saving a suggestion")
    public ResponseEntity<?> save(@RequestBody @Validated(ValidationGroup.Save.class) SuggestionSaveUpdateRequest suggestion) {
        UserDetailsImpl principal = securityContextUtil.getCurrentUser();
        Suggestion saved = suggestionService.registerSuggestionForOrder(suggestion,principal);
        return ResponseEntity.ok(suggestionMapper.mapToResponse(saved));
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @PutMapping("/update")
    @Operation(summary = "Update suggestion", description = "Method for updating a suggestion")
    public ResponseEntity<SuggestionFindResponse> update(@RequestBody @Validated(ValidationGroup.Update.class) SuggestionSaveUpdateRequest suggestion) {
        UserDetailsImpl principal = securityContextUtil.getCurrentUser();
        Suggestion updated = suggestionService.updateWithDTO(suggestion,principal.user().getId());
        return ResponseEntity.ok(suggestionMapper.mapToResponse(updated));
    }

    @PutMapping("/confirm-suggestion-acceptance")
    @Operation(summary = "Confirm suggestion acceptance", description = "Method for confirming a suggestion's acceptance")
    public ResponseEntity<String> confirmSuggestionAcceptance(@RequestParam Integer suggestionId) {
        suggestionService.confirmSuggestionAcceptance(suggestionId);
        return ResponseEntity.ok("Suggestion's acceptance has been confirmed");
    }

    @GetMapping("/find-all-by-expert-id")
    @Operation(summary = "Find all by expert id", description = "Finds all suggestions by expert id")
    public ResponseEntity<Page<SuggestionFindResponse>> findAllByExpertId(@RequestParam Integer expertId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(suggestionService.findAllByExpertId(expertId,PageRequest.of(page, size)));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("find-all-and-sort-by-price-ascending")
    @Operation(summary = "Find all and sort by price ascending", description = "Finds all suggestions sorted by price ascending")
    public ResponseEntity<Page<SuggestionFindResponse>> findAllAndSortByPriceAscending(@RequestParam Integer orderId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<Suggestion> allAndSortByPriceAsc = suggestionService.findAllAndSortByPriceAsc(orderId,PageRequest.of(page, size));
        return ResponseEntity.ok(allAndSortByPriceAsc.map(suggestionMapper::mapToResponse));
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/find-all-and-sort-by-expert-score-descending")
    @Operation(summary = "Find all and sort by expert score descending", description = "Finds all suggestions sorted by expert score descending")
    public ResponseEntity<Page<SuggestionFindResponse>> findAllAndSortByExpertScoreDescending(@RequestParam Integer orderId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<Suggestion> allByAndSortByExpertScoreDesc = suggestionService.findAllByAndSortByExpertScoreDesc(orderId, PageRequest.of(page, size));
        return ResponseEntity.ok(allByAndSortByExpertScoreDesc.map(suggestionMapper::mapToResponse));
    }
}
