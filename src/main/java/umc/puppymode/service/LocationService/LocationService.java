package umc.puppymode.service.LocationService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

public interface LocationService {
    ApiResponse<DrinkingAppointmentResponseDTO.StartAppointmentResultDTO> startAppointment(Long appointmentId, DrinkingAppointmentRequestDTO.StartAppointmentRequestDTO request);
}
