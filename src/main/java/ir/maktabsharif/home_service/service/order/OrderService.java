package ir.maktabsharif.home_service.service.order;

import ir.maktabsharif.home_service.base.service.BaseService;
import ir.maktabsharif.home_service.dto.order.OrderSaveUpdateRequest;
import ir.maktabsharif.home_service.dto.order.OrderSummaryDTO;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService extends BaseService<Order, Integer> {
    boolean existsBySpecialistAndOrderStatusIn(Integer expertId, List<OrderStatus> statuses);

    Page<Order> findByServiceId(Integer serviceId, Pageable pageable);

    Order saveWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest);

    void chooseExpert(Integer suggestionId);

    Page<OrderSummaryDTO> findAllByExpertId(Pageable pageable);

    Order updateWithDTO(OrderSaveUpdateRequest orderSaveUpdateRequest);

    Order updateStatusToStarted(Integer orderId);

    Order updateStatusToDone(Integer orderId);

    long reduce1ScoreFromExpertPerHour(Order order);

    Page<Order> findByCustomerId(Pageable pageable);

    boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId);
}
