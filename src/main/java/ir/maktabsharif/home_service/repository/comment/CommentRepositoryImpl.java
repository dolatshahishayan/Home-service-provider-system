package ir.maktabsharif.home_service.repository.comment;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.comment.Comment;
import jakarta.persistence.EntityManager;

public class CommentRepositoryImpl extends CrudRepositoryImpl<Comment> implements CommentRepository {
    public CommentRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Comment> getEntityClass() {
        return Comment.class;
    }
}
