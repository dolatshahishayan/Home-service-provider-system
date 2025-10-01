package ir.maktabsharif.home_service.model.user;

import ir.maktabsharif.home_service.model.enums.ExpertStatus;
import ir.maktabsharif.home_service.model.enums.Role;
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
public class Expert extends User {

    @Enumerated(EnumType.STRING)
    private ExpertStatus expertStatus;
    private Double score;
    private byte[] profilePictureData;


}
