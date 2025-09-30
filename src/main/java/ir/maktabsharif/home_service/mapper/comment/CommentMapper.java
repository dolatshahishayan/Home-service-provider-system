package ir.maktabsharif.home_service.mapper.comment;

import ir.maktabsharif.home_service.dto.comment.CommentFindResponse;
import ir.maktabsharif.home_service.dto.comment.CommentSaveUpdateRequest;
import ir.maktabsharif.home_service.model.comment.Comment;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    Comment mapToEntity(CommentSaveUpdateRequest commentSaveUpdateRequest);
    @Mapping(source = "order.id", target = "orderId")
    CommentFindResponse mapToResponse(Comment comment);
}