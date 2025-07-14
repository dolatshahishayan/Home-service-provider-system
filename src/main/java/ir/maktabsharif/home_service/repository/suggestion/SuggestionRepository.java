package ir.maktabsharif.home_service.repository.suggestion;

import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.suggestion.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Integer>, JpaSpecificationExecutor<Suggestion> {
    List<Suggestion> findAllByExpertId(Integer expertId);

    @Query("select s from Suggestion s where s.order = :order order by s.price asc")
    List<Suggestion> findAllByOrderAndSortByPriceAsc(@Param("order") Order order);

    @Query("select s from Suggestion s where s.order = :order order by s.expert.score desc")
    List<Suggestion> findAllByOrderAndSortByExpertScoreDesc(@Param("order") Order order);

    boolean existsByOrderIdAndExpertIdAndAcceptedTrue(Integer orderId, Integer expertId);
}
