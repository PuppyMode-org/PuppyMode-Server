package umc.puppymode.service.DrinkingAppointmentService;

import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

public interface DrinkingAppointmentCommendService {
    DrinkingAppointmentResponseDTO.AppointmentResultDTO createDrinkingAppointment(DrinkingAppointmentRequestDTO.AppointmentDTO request, Long userId);
    void deleteDrinkingAppointment(Long appointmentId);
    void rescheduleDrinkingAppointment(Long appointmentId, DrinkingAppointmentRequestDTO.RescheduleAppointmentRequestDTO request);
    void completeDrinkingAppointment(Long appointmentId);
}
