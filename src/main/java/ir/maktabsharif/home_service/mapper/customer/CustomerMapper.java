package ir.maktabsharif.home_service.mapper.customer;

import ir.maktabsharif.home_service.dto.customer.CustomerFindResponse;
import ir.maktabsharif.home_service.dto.customer.CustomerSaveUpdateRequest;
import ir.maktabsharif.home_service.model.user.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {
    Customer mapToEntity(CustomerSaveUpdateRequest customerSaveUpdateRequest);
    CustomerFindResponse mapToResponse(Customer customer);
}
