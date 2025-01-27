package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMDTO.FCMRequestDTO;
import umc.puppymode.web.dto.FCMDTO.FCMResponseDTO;

public interface FcmService {

    ApiResponse<FCMResponseDTO> sendMessageTo(FCMRequestDTO fcmRequestDTO);
}