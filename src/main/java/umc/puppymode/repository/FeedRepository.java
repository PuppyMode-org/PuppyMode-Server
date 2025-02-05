package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.Feed;
import umc.puppymode.web.dto.CalendarDTO.FeedDTO;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query("SELECT new umc.puppymode.web.dto.CalendarDTO.FeedDTO(f.feedingType, f.feedImageUrl) " +
            "FROM Feed f WHERE f.drinkHistory.drinkHistoryId = :drinkHistoryId")
    FeedDTO findFeedByHistoryId(@Param("drinkHistoryId") Long drinkHistoryId);

}
