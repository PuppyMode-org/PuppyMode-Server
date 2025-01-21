package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.DrinkHistory;

public interface DrinkHistoryRepository extends JpaRepository<DrinkHistory, Long> {
}
