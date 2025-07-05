package ir.maktabsharif.home_service.repository.comment;

import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Integer>, JpaSpecificationExecutor<Comment> {
    boolean existsByOrder(Order order);
    Optional<Comment> findByOrder(Order order);
}
