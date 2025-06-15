package ir.maktabsharif.home_service.model.user;

import ir.maktabsharif.home_service.model.subservice.SubService;
import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expert extends User {

    @Enumerated(EnumType.STRING)
    private ExpertStatus expertStatus;
    private byte[] profilePicture;
}
