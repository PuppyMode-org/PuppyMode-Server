package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.User;
import umc.puppymode.domain.UserAuth;
import umc.puppymode.domain.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByUser_EmailAndAuthProvider(String email, AuthProvider authProvider);

    @Query("SELECT u.authId FROM UserAuth u WHERE u.user.userId = :userId")
    Optional<String> findAuthIdByUserId(@Param("userId") Long userId);

    @Query("SELECT u.user FROM UserAuth u WHERE u.authProvider = :authProvider AND u.authId IN :authIds")
    List<User> findUsersByAuthProviderAndAuthIdIn(@Param("authProvider") AuthProvider authProvider, @Param("authIds") List<String> authIds);

    @Query("SELECT u.refreshToken FROM UserAuth u WHERE u.user.userId = :userId")
    Optional<String> findRefreshTokenByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserAuth u SET u.refreshToken = :refreshToken WHERE u.user.userId = :userId")
    void updateRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);

}