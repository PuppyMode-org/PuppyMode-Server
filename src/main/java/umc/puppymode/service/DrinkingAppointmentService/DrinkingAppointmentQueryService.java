package umc.puppymode.service.DrinkingAppointmentService;

import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

public interface DrinkingAppointmentQueryService {
    DrinkingAppointmentResponseDTO.AppointmentResultDTO getDrinkingAppointmentById(Long appointmentId);
    DrinkingAppointmentResponseDTO.AppointmentListResultDTO getAllDrinkingAppointments(AppointmentStatus status, int page, int size);
}
