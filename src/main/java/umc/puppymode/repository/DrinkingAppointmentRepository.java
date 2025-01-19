package umc.puppymode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;

@Repository
public interface DrinkingAppointmentRepository extends JpaRepository<DrinkingAppointment, Long> {
    Page<DrinkingAppointment> findByStatus(AppointmentStatus status, Pageable pageable);
}
