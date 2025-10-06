package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.model.comment.Comment;

public interface CommentService extends BaseService<Comment,Integer> {
    boolean existsByOrder(Integer orderId);
    Comment findByOrder(Integer orderId);
    double viewExpertScoreByOrder(Integer orderId);
    Comment saveWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest,Integer userId);
    double viewExpertAverageScore(Integer userId);
    void deleteAll();
}
