package umc.puppymode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface PuppyRepository extends JpaRepository<Puppy, Long> {
    Optional<Puppy> findByUser(User user);
  
    @Query("SELECT p FROM Puppy p WHERE p.user.userId = :userId")
    Optional<Puppy> findByUserId(@Param("userId")Long userId);
}

