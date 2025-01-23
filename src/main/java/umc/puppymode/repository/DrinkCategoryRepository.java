package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.DrinkCategory;

public interface DrinkCategoryRepository extends JpaRepository<DrinkCategory, Long> {
}
