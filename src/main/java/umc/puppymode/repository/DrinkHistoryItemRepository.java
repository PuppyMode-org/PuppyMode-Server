package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.puppymode.domain.DrinkHistoryItem;
import umc.puppymode.web.dto.CalenderDTO.CalenderResponseDTO;
import umc.puppymode.web.dto.CalenderDTO.DrinkHistoryItemDTO;

import java.util.List;

@Repository
public interface DrinkHistoryItemRepository extends JpaRepository<DrinkHistoryItem, Long> {
    @Query("SELECT new umc.puppymode.web.dto.CalenderDTO.DrinkHistoryItemDTO(hi.item.itemName, hi.unit, hi.value, hi.safetyValue, hi.maxValue) " +
            "FROM DrinkHistoryItem hi WHERE hi.history.drinkHistoryId = :drinkHistoryId")
    List<DrinkHistoryItemDTO> findDrinkItemsByHistoryId(@Param("drinkHistoryId") Long drinkHistoryId);
    List<DrinkHistoryItem> findByHistory_User_UserId(Long userId);
    List<DrinkHistoryItem> findByHistory_User_UserIdAndItem_ItemId(Long userId, Long drinkItemId);
}
