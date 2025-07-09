package ir.maktabsharif.home_service.mapper.user;

import ir.maktabsharif.home_service.dto.user.UserFindResponse;
import ir.maktabsharif.home_service.dto.user.UserSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import ir.maktabsharif.home_service.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User mapToEntity(UserSaveUpdateRequest userSaveUpdateRequest);

    void updateEntityWithDTO(UserSaveUpdateRequest userSaveUpdateRequest, @MappingTarget User user);

    UserFindResponse mapToResponse(User user);

    UserSearchResponseDTO mapExpertToSearchResponse(Expert expert);

    UserSearchResponseDTO mapCustomerToSearchResponse(Customer customer);
}
