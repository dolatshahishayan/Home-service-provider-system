package ir.maktabsharif.home_service.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.ValidationGroup;
import ir.maktabsharif.home_service.dto.comment.CommentFindResponse;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.service.comment.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Comment controller", description = "Controller class for comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/save")
    @Operation(summary = "Save comment", description = "Save method for comment")
    public ResponseEntity<?> save(@RequestBody @Validated(ValidationGroup.save.class) CommentSaveUpdateRequest commentSaveUpdateRequest, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        return ResponseEntity.ok(commentMapper.mapToResponse(commentService.saveWithDTO(commentSaveUpdateRequest, currentUser)));
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

    @GetMapping("/view-expert-score-by-order")
    @Operation(summary = "View expert score", description = "View expert score by order")
    public ResponseEntity<Double> viewExpertScoreByOrder(@RequestParam Integer orderId) {
        return ResponseEntity.ok(commentService.viewExpertScoreByOrder(orderId));
    }
}
