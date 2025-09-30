package ir.maktabsharif.home_service.mapper.user;

import ir.maktabsharif.home_service.dto.user.UserSearchResponseDTO;
import ir.maktabsharif.home_service.model.user.Customer;
import ir.maktabsharif.home_service.model.user.Expert;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserSearchResponseDTO mapExpertToSearchResponse(Expert expert);

    UserSearchResponseDTO mapCustomerToSearchResponse(Customer customer);
}