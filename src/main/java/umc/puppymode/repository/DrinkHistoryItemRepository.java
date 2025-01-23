package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.DrinkHistoryItem;

public interface DrinkHistoryItemRepository extends JpaRepository<DrinkHistoryItem, Long> {
}
