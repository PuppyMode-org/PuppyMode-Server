package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.User;
import umc.puppymode.domain.UserAuthProvider;

import java.util.List;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    @Query("SELECT u.user FROM UserAuthProvider u WHERE u.authProvider = :provider AND u.authId IN :authIds")
    List<User> findUsersByAuthProviderAndAuthIdIn(@Param("provider") String provider, @Param("authIds") List<String> authIds);
}