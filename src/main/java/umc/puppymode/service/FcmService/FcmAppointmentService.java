package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMAppointmentRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

public interface FcmAppointmentService {

    ApiResponse<FCMResponseDTO> scheduleDrinkingNotifications(
            FCMAppointmentRequestDTO fcmAppointmentRequestDTO
    );
}
