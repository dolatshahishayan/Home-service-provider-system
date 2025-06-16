package ir.maktabsharif.home_service.mapper.user;

import ir.maktabsharif.home_service.dto.user.UserFindResponse;
import ir.maktabsharif.home_service.dto.user.UserSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User mapToEntity(UserSaveUpdateRequest userSaveUpdateRequest);
    UserFindResponse mapToDTO(User user);
}
