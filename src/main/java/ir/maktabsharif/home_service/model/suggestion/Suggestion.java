package ir.maktabsharif.home_service.model.suggestion;

import ir.maktabsharif.home_service.model.order.Order;
import ir.maktabsharif.home_service.model.user.Expert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    @ManyToOne
    private Expert expert;
    @ManyToOne
    private Order order;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime startDate;

    private Double price;

    private Double workDuration;

    private Boolean accepted;


}
