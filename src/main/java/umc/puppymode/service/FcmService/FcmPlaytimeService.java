package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMPlayRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

public interface FcmPlaytimeService {
    ApiResponse<FCMResponseDTO> schedulePushAtSpecificTime(FCMPlayRequestDTO fcmPlayRequestDTO);
}
