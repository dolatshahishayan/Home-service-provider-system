package ir.maktabsharif.home_service.service.comment;

import ir.maktabsharif.home_service.base.service.BaseServiceImpl;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.mapper.comment.CommentMapper;
import ir.maktabsharif.home_service.model.comment.Comment;
import ir.maktabsharif.home_service.repository.comment.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment, CommentSaveUpdateRequest, CommentRepository, CommentMapper> implements CommentService {
    public CommentServiceImpl(CommentRepository repository, CommentMapper mapper) {
        super(repository, mapper);
    }

    public void saveWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest) {
        save(mapper.mapToEntity(commentSaveUpdateRequest));
    }

      public void updateWithDTO(CommentSaveUpdateRequest commentSaveUpdateRequest) {
        update(findById(commentSaveUpdateRequest.getId()));
    }
}
