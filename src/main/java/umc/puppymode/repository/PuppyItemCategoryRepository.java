package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.puppymode.domain.PuppyItemCategory;

public interface PuppyItemCategoryRepository extends JpaRepository<PuppyItemCategory, Long> {
}
