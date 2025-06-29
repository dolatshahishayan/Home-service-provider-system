package ir.maktabsharif.home_service.mapper.comment;

import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.model.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    Comment mapToEntity(CommentSaveUpdateRequest commentSaveUpdateRequest);
}
