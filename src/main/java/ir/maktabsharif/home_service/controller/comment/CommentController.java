package ir.maktabsharif.home_service.controller.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.maktabsharif.home_service.dto.comment.CommentFindResponse;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.service.comment.CommentService;
import ir.maktabsharif.home_service.service.order.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Comment controller", description = "Controller class for comment")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final OrderService orderService;

    @PostMapping("/save-comment")
    @Operation(summary = "save comment", description = "save method for comment")
    public ResponseEntity<?> save(@RequestBody CommentSaveUpdateRequest commentSaveUpdateRequest, HttpSession session) {
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        return ResponseEntity.ok(commentMapper.mapToResponse(commentService.saveWithDTO(commentSaveUpdateRequest, currentUser)));
    }

    @GetMapping("/exists-by-order")
    @Operation(summary = "exist by order", description = "checks if order has a registered comment or not")
    public ResponseEntity<Boolean> existsByOrder(@RequestParam Integer orderId) {
        Order byId = orderService.findById(orderId);
        return ResponseEntity.ok(commentService.existsByOrder(byId));
    }

    @GetMapping("/find-by-order")
    @Operation(summary = "find by order", description = "find an order's comment")
    public ResponseEntity<CommentFindResponse> findByOrder(@RequestParam Integer orderId) {
        Order byId = orderService.findById(orderId);
        return ResponseEntity.ok(commentMapper.mapToResponse(commentService.findByOrder(byId)));
    }

    @GetMapping("/view-expert-score-by-order")
    @Operation(summary = "view expert score",description = "view expert score by order")
    public ResponseEntity<Double> viewExpertScoreByOrder(@RequestParam Integer orderId) {
        return ResponseEntity.ok(commentService.viewExpertScoreByOrder(orderId));
    }
}
