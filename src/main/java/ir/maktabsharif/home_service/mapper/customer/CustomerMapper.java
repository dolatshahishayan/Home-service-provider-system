package ir.maktabsharif.home_service.mapper.customer;

import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
    Customer mapToEntity(CustomerSaveUpdateRequest customerSaveUpdateRequest);
    void updateEntityWithDTO(CustomerSaveUpdateRequest customerSaveUpdateRequest, @MappingTarget Customer customer);
    CustomerFindResponse mapToResponse(Customer customer);
}
