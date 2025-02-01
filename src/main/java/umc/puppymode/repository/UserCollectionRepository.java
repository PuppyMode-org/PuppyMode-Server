package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.User;
import umc.puppymode.domain.mapping.UserCollection;

import java.util.List;

public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {

    @Query("SELECT u FROM UserCollection u WHERE u.user = :user ORDER BY u.isCompleted ASC, (u.collection.requiredNum - u.currentNum) ASC, u.userCollectionId ASC")
        // 달성되지 않은 것부터 보여주되, 그 내에서는 달성까지 적게 남은 것부터 보여주고, 그 내에서는 id 기준으로 정렬
    List<UserCollection> findByUserOrderByIsCompleted(@Param("user") User user);

    List<UserCollection> findByUser(User user);
}
