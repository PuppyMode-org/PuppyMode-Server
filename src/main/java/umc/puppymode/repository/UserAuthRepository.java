package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.User;
import umc.puppymode.domain.UserAuth;
import umc.puppymode.domain.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByUser_EmailAndAuthProvider(String email, AuthProvider authProvider);

    @Query("SELECT u.user FROM UserAuth u WHERE u.authProvider = :provider AND u.authId IN :authIds")
    List<User> findUsersByAuthProviderAndAuthIdIn(@Param("provider") String provider, @Param("authIds") List<String> authIds);
}