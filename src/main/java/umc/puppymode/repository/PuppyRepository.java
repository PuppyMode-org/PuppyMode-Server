package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.puppymode.domain.Puppy;

import java.util.Optional;

public interface PuppyRepository extends JpaRepository<Puppy, Long> {

    @Query("SELECT p FROM Puppy p WHERE p.user.userId = :userId")
    Optional<Puppy> findByUserId(Long userId);

}
