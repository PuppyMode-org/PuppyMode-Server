package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.Token;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.domain.enums.TokenType;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.repository.TokenRepository;
import umc.puppymode.util.RandomMessages;
import umc.puppymode.web.dto.FCMDTO.FCMAppointmentRequestDTO;
import umc.puppymode.web.dto.FCMDTO.FCMAppointmentResponseDTO;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmAppointmentServiceImpl implements FcmAppointmentService {

    private final FcmService fcmService;
    private final DrinkingAppointmentRepository drinkingAppointmentRepository;
    private final TokenRepository tokenRepository;
    private final ScheduledExecutorService scheduler;

    @Override
    public ApiResponse<FCMAppointmentResponseDTO> scheduleDrinkingNotifications() {
        try {
            // 진행 중인 술 약속 조회
            List<DrinkingAppointment> ongoingAppointments = drinkingAppointmentRepository.findByStatusWithUser(AppointmentStatus.ONGOING);

            // 진행 중인 술 약속이 없는 경우 예외 처리
            if (ongoingAppointments.isEmpty()) {
                throw new GeneralException(ErrorStatus.APPOINTMENT_TIME_MISMATCH);
            }

            // 술 약속에 포함된 사용자 리스트 생성
            List<User> users = ongoingAppointments.stream()
                    .map(DrinkingAppointment::getUser)
                    .toList();

            // 사용자별 FCM 토큰 조회
            List<Token> fcmTokens = tokenRepository.findByUsersAndTokenType(users, TokenType.FCM);

            // 사용자 ID와 FCM 토큰 매핑
            Map<Long, Token> userTokenMap = fcmTokens.stream()
                    .collect(Collectors.toMap(
                            token -> token.getUser().getUserId(),
                            token -> token
                    ));

            List<Long> appointmentIdsWithNotifications = new ArrayList<>();

            // 술 약속별 푸시 알림 전송
            for (DrinkingAppointment appointment : ongoingAppointments) {
                Token fcmToken = userTokenMap.get(appointment.getUser().getUserId());
                if (fcmToken != null) {
                    FCMAppointmentRequestDTO fcmAppointmentRequestDTO = createFCMRequestDTO(fcmToken.getToken());

                    // 초기 알림 전송
                    sendInitialNotificationAsync(fcmAppointmentRequestDTO);

                    // 랜덤 알림 스케줄링
                    scheduleRandomNotificationsAsync(fcmAppointmentRequestDTO);

                    // 알림이 전송된 술 약속 ID 추가
                    appointmentIdsWithNotifications.add(appointment.getAppointmentId());
                }
            }

            FCMAppointmentResponseDTO responseDTO = new FCMAppointmentResponseDTO(
                    "진행 중인 술 약속에 대해 푸시 알림이 전송되었습니다.",
                    appointmentIdsWithNotifications
            );

            return ApiResponse.onSuccess(responseDTO);

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
        }
    }

    private FCMAppointmentRequestDTO createFCMRequestDTO(String fcmToken) {
        return FCMAppointmentRequestDTO.builder()
                .token(fcmToken)
                .title("음주 약속 알림")
                .body("약속이 진행 중입니다!")
                .image("https://d1le4wcgenmery.cloudfront.net/5527d0c2-8511-463b-b456-628ee5c18716PuppyModeLogo.png")
                .build();
    }

    @Async
    public void sendInitialNotificationAsync(FCMAppointmentRequestDTO fcmAppointmentRequestDTO) {
        try {
            fcmService.sendMessageTo(
                    FCMAppointmentRequestDTO.builder()
                            .token(fcmAppointmentRequestDTO.getToken())
                            .title("음주 시작을 확인했어요!")
                            .body("아직 음주 중이 아니라면 미루기를 눌러주세요.")
                            .image("https://d1le4wcgenmery.cloudfront.net/5527d0c2-8511-463b-b456-628ee5c18716PuppyModeLogo.png")
                            .build()
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
        }
    }

    @Async
    public CompletableFuture<Void> scheduleRandomNotificationsAsync(FCMAppointmentRequestDTO fcmAppointmentRequestDTO) {
        ZonedDateTime startTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        for (int i = 0; i < 5; i++) {
            long delay = i + 1;
            try {
                scheduler.schedule(() -> {
                    try {
                        // 경과 시간 계산
                        long minutesElapsed = Duration.between(
                                startTime,
                                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                        ).toMinutes();

                        // 랜덤 메시지 생성
                        RandomMessages randomMessage = RandomMessages.getRandom();
                        String messageToSend;

                        if (randomMessage == RandomMessages.DRINK_TIME_ELAPSED) {
                            messageToSend = String.format(
                                    "벌써 음주하신 지 %d분 지났어요!",
                                    minutesElapsed
                            );
                        } else {
                            messageToSend = randomMessage.getMessage();
                        }

                        fcmService.sendMessageTo(
                                FCMAppointmentRequestDTO.builder()
                                        .token(fcmAppointmentRequestDTO.getToken())
                                        .title(messageToSend)
                                        .body("음주를 마무리하셨다면 음주 종료하기를 눌러주세요.")
                                        .image("https://d1le4wcgenmery.cloudfront.net/5527d0c2-8511-463b-b456-628ee5c18716PuppyModeLogo.png")
                                        .build()
                        );

                        System.out.println("음주 알림 전송 시간: " + minutesElapsed + "분");

                    } catch (Exception ex) {
                        System.err.println("알림 전송 실패. 다시 시도합니다: " + ex.getMessage());
                        scheduler.schedule(() -> scheduleRandomNotificationsAsync(fcmAppointmentRequestDTO), 1, TimeUnit.MINUTES);
                    }
                }, delay, TimeUnit.MINUTES);

            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SCHEDULE_FAILED);
            }
        }

        return CompletableFuture.completedFuture(null);
    }
}
