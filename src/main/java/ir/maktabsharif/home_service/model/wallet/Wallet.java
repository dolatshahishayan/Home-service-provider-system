package ir.maktabsharif.home_service.model.wallet;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

public class Wallet  {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    private Double balance;

    @OneToOne
    private User user;

    public Wallet() {
    }

    public Wallet(Integer id, Double balance, User user) {
        this.id = id;
        this.balance = balance;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
