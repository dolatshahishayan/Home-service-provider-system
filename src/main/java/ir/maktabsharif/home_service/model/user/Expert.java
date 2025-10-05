package ir.maktabsharif.home_service.model.user;

import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.Role;
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
public class Expert extends User {

    @Enumerated(EnumType.STRING)
    private ExpertStatus expertStatus;
    private BigDecimal score;


}
