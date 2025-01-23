package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.HangoverItem;

public interface HangoverRepository extends JpaRepository<HangoverItem, Long> {
}
