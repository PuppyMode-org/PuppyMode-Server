package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.service.UserService.UserQueryService;
import umc.puppymode.web.dto.FCMPlayRequestDTO;
import umc.puppymode.web.dto.FCMRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmPlaytimeServiceImpl implements FcmPlaytimeService {

    private final FcmService fcmService;
    private final UserQueryService userQueryService;

    @Override
    public ApiResponse<FCMResponseDTO> schedulePushAtSpecificTime(FCMPlayRequestDTO fcmPlayRequestDTO) {
        try {
            // 푸시 알림 전송
            return fcmService.sendMessageTo(fcmPlayRequestDTO);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
        }
    }

    @Scheduled(cron = "0 0 10 * * ?", zone = "Asia/Seoul")
    public void scheduleDailyPushNotification() {
        try {
            // 모든 FCM 토큰 조회
            List<String> fcmTokens = userQueryService.getAllFcmTokens();
            if (fcmTokens.isEmpty()) {
                throw new GeneralException(ErrorStatus.FIREBASE_MISSING_TOKEN);
            }

            String puppyNickname = "브로콜리"; // puppy 관련 API 구현 후 수정 필요

            // FCM 토큰마다 푸시 알림 전송
            fcmTokens.stream()
                    .map(token -> FCMPlayRequestDTO.builder()
                            .token(token)
                            .title("오늘도 " + puppyNickname + "랑 놀아주세요.")
                            .body(puppyNickname + "가 주인님을 애타게 기다리고 있어요.")
                            .image("이미지 URL")
                            .build())
                    .forEach(fcmService::sendMessageTo);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_SCHEDULE_ERROR);
        }
    }
}
