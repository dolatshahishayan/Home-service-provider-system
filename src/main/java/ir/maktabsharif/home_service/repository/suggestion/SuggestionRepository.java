package ir.maktabsharif.home_service.repository.suggestion;

import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SuggestionRepository extends JpaRepository<Suggestion, Integer>, JpaSpecificationExecutor<Suggestion> {
    Page<Suggestion> findAllByExpertId(Integer expertId, Pageable pageable);

    Optional<Suggestion> findByOrderId(Integer orderId);

    @Query("select s from Suggestion s where s.order = :order order by s.price asc")
    Page<Suggestion> findAllByOrderAndSortByPriceAsc(@Param("order") Order order,Pageable pageable);

    @Query("select s from Suggestion s where s.order = :order order by s.expert.score desc")
    Page<Suggestion> findAllByOrderAndSortByExpertScoreDesc(@Param("order") Order order,Pageable pageable);

    boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId, Integer expertId);
}
