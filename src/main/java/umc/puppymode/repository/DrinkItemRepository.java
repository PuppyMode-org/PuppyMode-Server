package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.DrinkItem;

public interface DrinkItemRepository extends JpaRepository<DrinkItem, Long> {
}
