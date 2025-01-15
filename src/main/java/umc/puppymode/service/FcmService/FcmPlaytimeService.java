package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMPlayResponseDTO;

public interface FcmPlaytimeService {
    ApiResponse<FCMPlayResponseDTO> schedulePushAtSpecificTime();
}
