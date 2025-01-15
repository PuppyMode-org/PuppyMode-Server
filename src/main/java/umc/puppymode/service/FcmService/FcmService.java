package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

public interface FcmService {

    ApiResponse<FCMResponseDTO> sendMessageTo(FCMRequestDTO fcmRequestDTO);
}