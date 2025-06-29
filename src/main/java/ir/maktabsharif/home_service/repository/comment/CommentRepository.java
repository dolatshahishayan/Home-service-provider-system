package ir.maktabsharif.home_service.repository.comment;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.order.Order;

public interface CommentRepository extends CrudRepository<Comment> {
    boolean existsByOrder(Order order);
    Comment findByOrder(Order order);
}
