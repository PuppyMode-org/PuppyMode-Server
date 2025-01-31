package umc.puppymode.service.DrinkingAppointmentService;

import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

public interface DrinkingAppointmentCommendService {
    DrinkingAppointmentResponseDTO.AppointmentResultDTO createDrinkingAppointment(DrinkingAppointmentRequestDTO.AppointmentDTO request, Long userId);
    void deleteDrinkingAppointment(Long appointmentId, Long userId);
    void completeDrinkingAppointment(Long appointmentId, Long userId);
    DrinkingAppointmentResponseDTO.UpdateAppointmentResultDTO updateDrinkingAppointment(Long appointmentId, DrinkingAppointmentRequestDTO.UpdateAppointmentDTO request, Long userId);
}
