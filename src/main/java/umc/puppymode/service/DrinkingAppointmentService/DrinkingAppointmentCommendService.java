package umc.puppymode.service.DrinkingAppointmentService;

import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

public interface DrinkingAppointmentCommendService {
    DrinkingAppointmentResponseDTO.AppointmentResultDTO createDrinkingAppointment(DrinkingAppointmentRequestDTO.AppointmentDTO request);
    void deleteDrinkingAppointment(Long appointmentId);
}