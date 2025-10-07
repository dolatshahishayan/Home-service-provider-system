package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSearchRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService extends BaseService<Order, Integer> {
    boolean existsBySpecialistAndOrderStatusIn(Integer expertId, List<OrderStatus> statuses);

    Page<Order> findByServiceId(Integer serviceId, Pageable pageable);

    Order saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest,Integer userId);

    void chooseExpert(Integer suggestionId);

    Page<OrderSummaryDTO> findAllByExpertId(Pageable pageable,Integer userId);

    Order updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest,Integer userId);

    Order updateStatusToStarted(Integer orderId,Integer userId);

    Order updateStatusToDone(Integer orderId,Integer userId);

    long reduce1ScoreFromExpertPerHour(Suggestion suggestion);

    Page<OrderSummaryDTO> searchOrders(OrderSearchRequest request, Pageable pageable);

    Page<Order> findByCustomerId(OrderStatus status, Pageable pageable,Integer userId);

    boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId,Integer userId);

    void deleteAll();
}
