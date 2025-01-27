package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.puppymode.domain.Token;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.TokenType;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);

    @Query("SELECT t.token FROM Token t WHERE t.tokenType = 'FCM' AND t.user.receiveNotifications = true")
    List<String> findAllFcmTokensWithNotification();

    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);
}
