package ir.maktabsharif.home_service.repository.order;

import ir.maktabsharif.home_service.base.repository.CrudRepository;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order> {
    boolean existsBySpecialistAndOrderStatusIn(Expert expert, List<OrderStatus> statuses);
    List<Order> findByServiceId(Integer serviceId);
}
