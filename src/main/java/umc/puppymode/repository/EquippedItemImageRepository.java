package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.puppymode.domain.EquippedItemImage;
import umc.puppymode.domain.enums.PuppyType;

import java.util.Optional;

@Repository
public interface EquippedItemImageRepository extends JpaRepository<EquippedItemImage, Long> {
    /**
     * 강아지 타입, 레벨 이름, 아이템 카테고리에 따라 착용 이미지를 조회합니다.
     *
     * @param puppyType 강아지 타입
     * @param levelName 강아지 레벨 이름
     * @param itemId 아이템 아이디
     * @return imageUrl
     */
    Optional<EquippedItemImage> findByPuppyTypeAndLevelNameAndItemId(
            PuppyType puppyType,
            String levelName,
            Long itemId
    );
}

