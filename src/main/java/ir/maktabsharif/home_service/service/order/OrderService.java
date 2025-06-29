package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;

import java.util.List;

public interface OrderService extends BaseService<Order, OrderSaveUpdateRequest> {
    boolean existsBySpecialistAndOrderStatusIn(Expert expert, List<OrderStatus> statuses);
    List<Order> findByServiceId(Integer serviceId);
    void saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest);
    void chooseExpert(Integer suggestionId);
    List<OrderFindResponse> findAllByExpertId(Integer expertId);
    void updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest);
}
