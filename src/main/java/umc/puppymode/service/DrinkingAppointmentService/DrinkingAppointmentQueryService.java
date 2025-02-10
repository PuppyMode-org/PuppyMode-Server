package umc.puppymode.service.DrinkingAppointmentService;

import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

import java.util.Optional;

public interface DrinkingAppointmentQueryService {
    DrinkingAppointmentResponseDTO.AppointmentResultDTO getDrinkingAppointmentById(Long appointmentId, Long userId);
    DrinkingAppointmentResponseDTO.AppointmentListResultDTO getAllDrinkingAppointments(AppointmentStatus status, int page, int size, Long userId);
    boolean isDrinkingActive(Long appointmentId, Long userId);
    int getDrinkingDuration(Long appointmentId, Long userId);
    Optional<DrinkingAppointmentResponseDTO.AppointmentSimpleDTO> getNearestScheduledAppointmentForToday(Long userId);
}
