package ir.maktabsharif.home_service.repository.order;

import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer>, JpaSpecificationExecutor<Order> {
    boolean existsByExpertAndOrderStatusIn(Expert expert, List<OrderStatus> statuses);
    List<Order> findByServiceId(Integer serviceId);
    List<Order> findByCustomerId(Integer customerId);
}
