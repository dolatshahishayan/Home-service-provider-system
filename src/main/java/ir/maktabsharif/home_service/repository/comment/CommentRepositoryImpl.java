package ir.maktabsharif.home_service.repository.comment;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.model.order.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryImpl extends CrudRepositoryImpl<Comment> implements CommentRepository {
    public CommentRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Comment> getEntityClass() {
        return Comment.class;
    }

    @Override
    public boolean existsByOrder(Order order) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Comment> root = query.from(Comment.class);

        Predicate orderPredicate = cb.equal(root.get("order"), order);
        query.select(cb.count(root)).where(orderPredicate);

        Long count = em.createQuery(query).getSingleResult();
        return count > 0;
    }

    @Override
    public Comment findByOrder(Order order) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Comment> query = cb.createQuery(Comment.class);
        Root<Comment> root = query.from(Comment.class);
        Predicate orderPredicate = cb.equal(root.get("order"), order);
        query.select(root).where(orderPredicate);
        return em.createQuery(query).getSingleResult();
    }

}
