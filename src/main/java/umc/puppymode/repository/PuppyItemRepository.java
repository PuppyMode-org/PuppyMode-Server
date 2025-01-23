package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.PuppyItem;

import java.util.List;

public interface PuppyItemRepository extends JpaRepository<PuppyItem, Long> {
    // 특정 카테고리의 아이템 수를 계산
    Long countByCategory_CategoryId(Long categoryId);

    // 특정 카테고리의 모든 아이템 조회
    List<PuppyItem> findAllByCategory_CategoryId(Long categoryId);
}
