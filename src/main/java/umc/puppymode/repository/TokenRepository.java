package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.Token;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.TokenType;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);
}
