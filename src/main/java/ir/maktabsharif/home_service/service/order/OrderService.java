package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.dto.user.UserSessionDTO;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;

import java.util.List;

public interface OrderService extends BaseService<Order, Integer> {
    boolean existsBySpecialistAndOrderStatusIn(Integer expertId, List<OrderStatus> statuses);

    List<Order> findByServiceId(Integer serviceId);

    Order saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest, Integer customerId);

    void chooseExpert(Integer suggestionId);

    List<OrderSummaryDTO> findAllByExpertId(Integer expertId);

    Order updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest, Integer customerId);

    Order updateStatusToStarted(Integer orderId, UserSessionDTO currentUser);

    Order updateStatusToDone(Integer orderId, UserSessionDTO currentUser);

    void reduce1ScoreFromExpertPerHour(Integer orderId);

    List<Order> findByCustomerId(Integer customerId);

    boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId, Integer expertId);
}
