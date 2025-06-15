package ir.maktabsharif.home_service.model.user;

import ir.maktabsharif.home_service.model.order.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends User{
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;
}
