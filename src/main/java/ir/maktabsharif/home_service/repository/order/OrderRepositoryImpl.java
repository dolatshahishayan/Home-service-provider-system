package ir.maktabsharif.home_service.repository.order;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.order.Order;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl extends CrudRepositoryImpl<Order> implements OrderRepository {
    public OrderRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Order> getEntityClass() {
        return Order.class;
    }
}
