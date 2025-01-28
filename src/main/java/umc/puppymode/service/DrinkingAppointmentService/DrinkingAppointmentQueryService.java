package umc.puppymode.service.DrinkingAppointmentService;

import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

public interface DrinkingAppointmentQueryService {
    DrinkingAppointmentResponseDTO.AppointmentResultDTO getDrinkingAppointmentById(Long appointmentId, Long userId);
    DrinkingAppointmentResponseDTO.AppointmentListResultDTO getAllDrinkingAppointments(AppointmentStatus status, int page, int size, Long userId);
    boolean isDrinkingActive(Long appointmentId, Long userId);
}
