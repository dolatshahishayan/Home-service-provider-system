package ir.maktabsharif.home_service.repository.order;

import ir.maktabsharif.home_service.base.repository.CrudRepositoryImpl;
import ir.maktabsharif.home_service.model.enums.OrderStatus;
import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl extends CrudRepositoryImpl<Order> implements OrderRepository {
    public OrderRepositoryImpl(EntityManager em) {
        super(em);
    }

    @Override
    public Class<Order> getEntityClass() {
        return Order.class;
    }

    @Override
    public boolean existsBySpecialistAndOrderStatusIn(Expert expert, List<OrderStatus> statuses) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Order> root = query.from(Order.class);

        Predicate specialistPredicate = cb.equal(root.get("expert"), expert);
        Predicate statusPredicate = root.get("orderStatus").in(statuses);

        query.select(cb.count(root)).where(cb.and(specialistPredicate, statusPredicate));

        Long count = em.createQuery(query).getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<Order> findByServiceId(Integer serviceId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);
        query.select(root).where(cb.and(cb.equal(root.get("serviceId"), serviceId)), cb.equal(root.get("orderStatus"), OrderStatus.WAITING_FOR_EXPERT_SUGGESTION));
        return em.createQuery(query).getResultList();
    }
}
