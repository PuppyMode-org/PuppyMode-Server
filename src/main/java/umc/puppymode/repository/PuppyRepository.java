package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;

import java.util.Optional;

public interface PuppyRepository extends JpaRepository<Puppy, Long> {
    Optional<Puppy> findByUser(User user);
}