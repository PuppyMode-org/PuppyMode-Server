package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.web.dto.FCMPlayRequestDTO;
import umc.puppymode.web.dto.FCMPlayResponseDTO;
import umc.puppymode.service.UserService.UserQueryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmPlaytimeServiceImpl implements FcmPlaytimeService {

    private final FcmService fcmService;
    private final UserQueryService userQueryService;

    @Override
    public ApiResponse<FCMPlayResponseDTO> schedulePlaytimeNotification() {
        FCMPlayResponseDTO responseDTO = new FCMPlayResponseDTO("푸시 알림이 매일 오전 10시에 전송되도록 예약되었습니다.");
        return ApiResponse.onSuccess(responseDTO);
    }

    @Override
    @Scheduled(cron = "0 0 10 * * ?", zone = "Asia/Seoul")
    public void sendScheduledPushNotification() {
        try {
            List<String> fcmTokens = userQueryService.getAllFcmTokens();

            // 토큰이 없으면 예외 처리
            if (fcmTokens.isEmpty()) {
                throw new GeneralException(ErrorStatus.FIREBASE_MISSING_TOKEN);
            }

            // 모든 FCM 토큰에 대해 알림 발송
            for (String token : fcmTokens) {
                try {
                    User user = userQueryService.getUserByFcmToken(token);

                    // 사용자 정보 없으면 건너뛰기
                    if (user == null) continue;

                    Puppy puppy = userQueryService.getUserPuppy(user.getUserId());

                    // 강아지 정보 없으면 건너뛰기
                    if (puppy == null) continue;

                    // 푸시 알림 발송
                    fcmService.sendMessageTo(
                            FCMPlayRequestDTO.builder()
                                    .token(token)
                                    .title("오늘도 " + puppy.getPuppyName() + "랑 놀아주세요.")
                                    .body(puppy.getPuppyName() + "가 주인님을 애타게 기다리고 있어요.")
                                    .image(puppy.getPuppyImageUrl())
                                    .build()
                    );
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_SCHEDULE_ERROR);
        }
    }
}
