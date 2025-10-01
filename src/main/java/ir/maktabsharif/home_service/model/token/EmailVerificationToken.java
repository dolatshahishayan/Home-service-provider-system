package ir.maktabsharif.home_service.model.token;

import ir.maktabsharif.home_service.model.user.User;
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
public class EmailVerificationToken {
    @Id
    @SequenceGenerator(name = "my_entity_seq_generator", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_entity_seq_generator")
    private Integer id;
    private String token;
    @OneToOne
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean used = false;


}
