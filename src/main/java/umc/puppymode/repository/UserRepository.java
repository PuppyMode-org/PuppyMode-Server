package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.puppymode.domain.User;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
    Optional<User> findByEmail(String email);

    @Query("SELECT u.fcmToken FROM User u WHERE u.receiveNotifications = true")
    List<String> findAllFcmTokensWithNotification();

    Optional<User> findByFcmToken(String fcmToken);
}
