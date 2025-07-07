package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.order.OrderFindResponse;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;

import java.util.List;

public interface OrderService extends BaseService<Order,Integer> {
    boolean existsBySpecialistAndOrderStatusIn(Integer expertId, List<OrderStatus> statuses);
    List<Order> findByServiceId(Integer serviceId);
    Order saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest);
    void chooseExpert(Integer suggestionId);
    List<OrderFindResponse> findAllByExpertId(Integer expertId);
    Order updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest);
    Order updateStatusToStarted(Integer orderId, UserSessionDTO currentUser);
    Order updateStatusToDone(Integer orderId,UserSessionDTO currentUser);
}
