package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMDTO.FCMAppointmentResponseDTO;

public interface FcmAppointmentService {

    ApiResponse<FCMAppointmentResponseDTO> scheduleDrinkingNotifications();
}
