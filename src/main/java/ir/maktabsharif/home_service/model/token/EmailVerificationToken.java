package ir.maktabsharif.home_service.model.token;

import ir.maktabsharif.home_service.base.model.BaseEntity;
import ir.maktabsharif.home_service.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

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
