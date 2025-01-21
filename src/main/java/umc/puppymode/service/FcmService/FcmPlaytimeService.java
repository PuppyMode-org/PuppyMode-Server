package umc.puppymode.service.FcmService;

import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.web.dto.FCMPlayResponseDTO;

public interface FcmPlaytimeService {

    // 스케줄된 시간에 실행될 알림 발송 메서드
    void sendScheduledPushNotification();

    // API 엔드포인트를 위한 스케줄 등록 메서드
    ApiResponse<FCMPlayResponseDTO> schedulePlaytimeNotification();
}