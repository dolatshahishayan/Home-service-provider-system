package ir.maktabsharif.home_service.repository.token;

import ir.maktabsharif.home_service.model.token.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer>, JpaSpecificationExecutor<EmailVerificationToken> {
    Optional<EmailVerificationToken> findByToken(String token);
}
