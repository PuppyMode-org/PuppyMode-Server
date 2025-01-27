package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMDTO.FCMResponseDTO;

public interface FcmAppointmentService {

    ApiResponse<FCMResponseDTO> scheduleDrinkingNotifications();
}
