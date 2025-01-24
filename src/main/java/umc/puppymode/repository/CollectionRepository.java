package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.Collection;
import umc.puppymode.domain.User;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    @Query("SELECT c FROM Collection c WHERE c.user = :user ORDER BY c.isCompleted ASC, (c.requiredNum - c.currentNum) ASC, c.collectionId ASC")
    // 달성되지 않은 것부터 보여주되, 그 내에서는 달성까지 적게 남은 것부터 보여주고, 그 내에서는 id 기준으로 정렬
    List<Collection> findByUserOrderByIsCompleted(@Param("user") User user);
}
