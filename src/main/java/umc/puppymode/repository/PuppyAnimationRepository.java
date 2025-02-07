package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.PuppyAnimation;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.enums.AnimationType;
import umc.puppymode.domain.enums.PuppyType;

import java.util.Optional;

public interface PuppyAnimationRepository extends JpaRepository<PuppyAnimation, Long> {
    Optional<PuppyAnimation> findByAnimationTypeAndPuppyTypeAndLevelName(AnimationType animationType, PuppyType puppyType, String levelName);
    Optional<PuppyAnimation> findByAnimationTypeAndPuppyTypeAndLevelNameAndItem(
            AnimationType animationType, PuppyType puppyType, String levelName, PuppyItem item);
}
