package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.PuppyItemCategory;
import umc.puppymode.domain.mapping.PuppyCustomization;

import java.util.List;
import java.util.Optional;

public interface PuppyCustomizationRepository extends JpaRepository<PuppyCustomization, Long> {
    boolean existsByPuppyAndPuppyItem(Puppy puppy, PuppyItem item);
    Optional<PuppyCustomization> findByPuppyAndPuppyItem(Puppy puppy, PuppyItem item);
    Optional<PuppyCustomization> findByPuppyAndPuppyItemCategoryAndIsEquippedTrue(Puppy puppy, PuppyItemCategory puppyItemCategory);
    List<PuppyCustomization> findByPuppy(Puppy puppy);
    List<PuppyCustomization> findByPuppyAndIsEquippedTrue(Puppy puppy);

    @Query("SELECT p FROM PuppyCustomization p WHERE p.puppy.puppyId = :puppyId")
    List<PuppyCustomization> findByPuppyId(@Param("puppyId")Long puppyId);
}
