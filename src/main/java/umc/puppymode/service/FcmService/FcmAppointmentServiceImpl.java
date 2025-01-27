package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.Token;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.domain.enums.TokenType;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.repository.TokenRepository;
import umc.puppymode.util.RandomMessages;
import umc.puppymode.web.dto.FCMDTO.FCMAppointmentRequestDTO;
import umc.puppymode.web.dto.FCMDTO.FCMResponseDTO;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FcmAppointmentServiceImpl implements FcmAppointmentService {

    private final FcmService fcmService;
    private final DrinkingAppointmentRepository drinkingAppointmentRepository;
    private final TokenRepository tokenRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Task scheduler

    @Override
    public ApiResponse<FCMResponseDTO> scheduleDrinkingNotifications() {
        try {
            List<DrinkingAppointment> ongoingAppointments = drinkingAppointmentRepository.findByStatus(AppointmentStatus.ONGOING);

            if (ongoingAppointments.isEmpty()) {
                throw new GeneralException(ErrorStatus.APPOINTMENT_TIME_MISMATCH);
            }

            // 각 약속에 대해 알림을 전송
            for (DrinkingAppointment appointment : ongoingAppointments) {
                if (appointment.getStatus() == AppointmentStatus.ONGOING) {
                    Long userId = appointment.getUser().getUserId();

                    // FCM 토큰 조회 부분
                    Token fcmToken = tokenRepository.findByUserAndTokenType(appointment.getUser(), TokenType.FCM)
                            .orElse(null);

                    if (fcmToken == null) {
                        continue; // 이 유저는 건너뜀
                    }

                    // FCM 알림 생성
                    FCMAppointmentRequestDTO fcmAppointmentRequestDTO = createFCMRequestDTO(fcmToken.getToken());

                    // 알림 전송
                    sendInitialNotification(fcmAppointmentRequestDTO);
                    scheduleRandomNotifications(fcmAppointmentRequestDTO);
                }
            }

            FCMResponseDTO response = FCMResponseDTO.builder()
                    .validateOnly(false)
                    .message(new FCMResponseDTO.Message(
                            new FCMResponseDTO.Notification(
                                    "음주 알림", "약속이 진행 중입니다.", null
                            ),
                            "allUsers"
                    ))
                    .build();

            return ApiResponse.onSuccess(response);
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
        }
    }

    private FCMAppointmentRequestDTO createFCMRequestDTO(String fcmToken) {
        return FCMAppointmentRequestDTO.builder()
                .token(fcmToken)  // 조회한 FCM 토큰 사용
                .title("음주 약속 알림")
                .body("약속이 진행 중입니다!")
                .build();
    }

    private void sendInitialNotification(FCMAppointmentRequestDTO fcmAppointmentRequestDTO) {
        try {
            fcmService.sendMessageTo(
                    FCMAppointmentRequestDTO.builder()
                            .token(fcmAppointmentRequestDTO.getToken())
                            .title("음주 시작을 확인했어요!")
                            .body("아직 음주 중이 아니라면 미루기를 눌러주세요.")
                            .build()
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
        }
    }

    private void scheduleRandomNotifications(FCMAppointmentRequestDTO fcmAppointmentRequestDTO) {
        ZonedDateTime startTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        for (int i = 0; i < 5; i++) {
            long delay = i + 1;
            try {
                scheduler.schedule(() -> {
                    // 경과 시간 계산
                    long minutesElapsed = Duration.between(startTime, ZonedDateTime.now(ZoneId.of("Asia/Seoul"))).toMinutes();

                    // 랜덤 메시지
                    RandomMessages randomMessage = RandomMessages.getRandom();
                    String messageToSend;

                    if (randomMessage == RandomMessages.DRINK_TIME_ELAPSED) {
                        messageToSend = String.format("벌써 음주하신 지 %d분 지났어요!", minutesElapsed);
                    } else {
                        messageToSend = randomMessage.getMessage();
                    }

                    fcmService.sendMessageTo(
                            FCMAppointmentRequestDTO.builder()
                                    .token(fcmAppointmentRequestDTO.getToken())
                                    .title(messageToSend)
                                    .body("음주를 마무리하셨다면 음주 종료하기를 눌러주세요.")
                                    .build()
                    );

                    // 경과 시간 출력
                    System.out.println("음주 알림 전송 시간: " + minutesElapsed + "분");
                }, delay, TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SCHEDULE_FAILED);
            }
        }
    }
}
