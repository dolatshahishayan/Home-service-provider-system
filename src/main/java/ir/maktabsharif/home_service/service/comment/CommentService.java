package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.order.Order;

public interface CommentService extends BaseService<Comment, CommentSaveUpdateRequest> {
    boolean existsByOrder(Order order);
    Comment findByOrder(Order order);
    double viewExpertScoreByOrder(Integer orderId);
    void saveWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest);
}
