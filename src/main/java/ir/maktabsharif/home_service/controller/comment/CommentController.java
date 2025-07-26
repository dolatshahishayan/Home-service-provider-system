package ir.maktabsharif.home_service.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.comment.CommentFindResponse;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.user.UserDetailsImpl;
import ir.maktabsharif.home_service.security.SecurityContextUtil;
import ir.maktabsharif.home_service.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments controller", description = "Controller class for comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final SecurityContextUtil  securityContextUtil;

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/save")
    @Operation(summary = "Save comment", description = "Save method for comment")
    public ResponseEntity<?> save(@RequestBody @Validated(ValidationGroup.Save.class) CommentSaveUpdateRequest commentSaveUpdateRequest) {
        UserDetailsImpl principal = securityContextUtil.getCurrentUser();
        return ResponseEntity.ok(commentMapper.mapToResponse(commentService.saveWithDTO(commentSaveUpdateRequest,principal.user().getId())));
    }

    @GetMapping("/exists-by-order")
    @Operation(summary = "Exist by order", description = "Checks if order has a registered comment or not")
    public ResponseEntity<Boolean> existsByOrder(@RequestParam Integer orderId) {
        return ResponseEntity.ok(commentService.existsByOrder(orderId));
    }

    @GetMapping("/find-by-order")
    @Operation(summary = "Find by order", description = "Find an order's comment")
    public ResponseEntity<CommentFindResponse> findByOrder(@RequestParam Integer orderId) {
        return ResponseEntity.ok(commentMapper.mapToResponse(commentService.findByOrder(orderId)));
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/view-expert-score-by-order")
    @Operation(summary = "View expert score", description = "View expert score by order")
    public ResponseEntity<Double> viewExpertScoreByOrder(@RequestParam Integer orderId) {
        return ResponseEntity.ok(commentService.viewExpertScoreByOrder(orderId));
    }

    @PreAuthorize("hasAuthority('ROLE_EXPERT')")
    @GetMapping("/view-average-score")
    @Operation(summary = "View average score",description = "View expert average score")
    public ResponseEntity<?> viewAverageScore() {
        UserDetailsImpl principal = securityContextUtil.getCurrentUser();
        return ResponseEntity.ok(commentService.viewExpertAverageScore(principal.user().getId()));
    }
}
