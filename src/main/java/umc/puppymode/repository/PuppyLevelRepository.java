package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.PuppyLevel;

import java.util.List;

public interface PuppyLevelRepository extends JpaRepository<PuppyLevel, Long> {

    List<PuppyLevel> findByPuppyLevel(Integer puppyLevel);

    // 시연을 위한 임시 코드
    PuppyLevel findByLevelName(String puppyLevelName);
}
