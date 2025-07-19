package ir.maktabsharif.home_service.repository.order;

import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    boolean existsByExpertAndOrderStatusIn(Expert expert, List<OrderStatus> statuses);

    Page<Order> findByServiceId(Integer serviceId, Pageable pageable);

    Page<Order> findByCustomerId(Integer customerId, Pageable pageable);

    Page<Order> findByExpertId(Integer expertId, Pageable pageable);
}
