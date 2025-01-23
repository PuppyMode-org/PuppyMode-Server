package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}
