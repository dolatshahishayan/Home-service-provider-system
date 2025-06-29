package ir.maktabsharif.home_service.mapper.order;

import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.model.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    Order mapToEntity(OrderSaveUpdateRequest orderSaveUpdateRequest);
    OrderFindResponse mapToResponse(Order order);
}
