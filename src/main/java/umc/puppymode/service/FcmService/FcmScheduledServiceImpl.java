package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.UserService.UserQueryService;
import umc.puppymode.web.dto.FCMRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmScheduledServiceImpl implements FcmScheduledService {

    private final FcmService fcmService;
    private final UserQueryService userQueryService;


    @Override
    public ApiResponse<FCMResponseDTO> schedulePushAtSpecificTime(FCMRequestDTO fcmRequestDTO) {
        return fcmService.sendMessageTo(fcmRequestDTO);
    }

    @Scheduled(cron = "0 8 2 * * ?", zone = "Asia/Seoul") // 매일 오전 10시 실행
    public void scheduleDailyPushNotification() {
        List<String> fcmTokens = userQueryService.getAllFcmTokens();
        String puppyNickname = "브로콜리";

        fcmTokens.stream()
                .map(token -> FCMRequestDTO.builder()
                        .token(token)
                        .title("오늘도 " + puppyNickname + "랑 놀아주세요.")
                        .body(puppyNickname + "가 주인님을 애타게 기다리고 있어요.")
                        .image("이미지 URL")
                        .build())
                .forEach(fcmService::sendMessageTo);
    }
}
