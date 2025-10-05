package ir.maktabsharif.home_service.model.wallet;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity {

    private BigDecimal balance;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

}
