package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.puppymode.domain.DrinkHistory;
import umc.puppymode.web.dto.CalendarDTO.CalendarListResponseDTO;

import java.util.List;

@Repository
public interface DrinkHistoryRepository extends JpaRepository<DrinkHistory, Long> {
    @Query("SELECT dh FROM DrinkHistory dh " +
            "LEFT JOIN FETCH dh.hangovers " +
            "WHERE dh.user.userId = :userId " +
            "AND FUNCTION('DATE_FORMAT', dh.drinkDate, '%Y-%m') = :month")
    List<DrinkHistory> findDrinkHistoriesByUserAndMonth(@Param("userId") Long userId, @Param("month") String month);
    @Query("SELECT dh FROM DrinkHistory dh " +
            "LEFT JOIN FETCH dh.hangovers " +
            "LEFT JOIN FETCH dh.feed " +
            "WHERE dh.drinkHistoryId = :drinkHistoryId AND dh.user.userId = :userId")
    DrinkHistory findDrinkHistoryDetail(@Param("userId") Long userId, @Param("drinkHistoryId") Long drinkHistoryId);

}
