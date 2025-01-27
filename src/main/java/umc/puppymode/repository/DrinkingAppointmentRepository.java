package umc.puppymode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DrinkingAppointmentRepository extends JpaRepository<DrinkingAppointment, Long> {
    Page<DrinkingAppointment> findByStatus(AppointmentStatus status, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM DrinkingAppointment a " +
            "WHERE a.user = :user AND DATE(a.dateTime) = :date")
    boolean existsByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT da FROM DrinkingAppointment da " +
            "JOIN FETCH da.user " +
            "WHERE da.status = :status")
    List<DrinkingAppointment> findByStatusWithUser(@Param("status") AppointmentStatus status);
}
