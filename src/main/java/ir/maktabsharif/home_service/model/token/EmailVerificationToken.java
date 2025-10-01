package ir.maktabsharif.home_service.model.token;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken extends BaseEntity {

    private String token;
    @OneToOne
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean used = false;


}
