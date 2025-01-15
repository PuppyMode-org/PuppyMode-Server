package umc.puppymode.service.FcmService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.util.DistanceCalculator;
import umc.puppymode.util.RandomMessages;
import umc.puppymode.web.dto.FCMAppointmentRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FcmAppointmentServiceImpl implements FcmAppointmentService {

    private final FcmService fcmService;
    private final DistanceCalculator distanceCalculator;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Task scheduler

    @Override
    public ApiResponse<FCMResponseDTO> scheduleDrinkingNotifications(
            FCMAppointmentRequestDTO fcmAppointmentRequestDTO
    ) {
        try {
            // 약속 장소 도착 확인 (1km 이내)
            // TODO: 사용자 현재 위치 API 구현 후 실제 위치로 수정 필요
            double userLatitude = 37.6378;
            double userLongitude = 127.0262;

            if (fcmAppointmentRequestDTO.getTargetLatitude() == null
                    || fcmAppointmentRequestDTO.getTargetLongitude() == null) {
                throw new GeneralException(ErrorStatus.INVALID_LOCATION_DATA);
            }

            // 위치가 약속 장소의 1km 이내에 도달했는지 체크
            if (!isWithinArrivalRange(userLatitude, userLongitude, fcmAppointmentRequestDTO)) {
                throw new GeneralException(ErrorStatus.LOCATION_NOT_IN_RANGE);
            }

            // 최초 알림 전송
            sendInitialNotification(fcmAppointmentRequestDTO);

            // 1시간 간격으로 추가 알림 전송
            scheduleRandomNotifications(fcmAppointmentRequestDTO);

            FCMResponseDTO response = FCMResponseDTO.builder()
                    .validateOnly(false)
                    .message(new FCMResponseDTO.Message(
                            new FCMResponseDTO.Notification(
                                    fcmAppointmentRequestDTO.getTitle(),
                                    fcmAppointmentRequestDTO.getBody(),
                                    fcmAppointmentRequestDTO.getImage()
                            ),
                            fcmAppointmentRequestDTO.getToken()
                    ))
                    .build();

            return ApiResponse.onSuccess(response);
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
        }
    }

    private boolean isWithinArrivalRange(
            double userLatitude, double userLongitude, FCMAppointmentRequestDTO fcmAppointmentRequestDTO
    ) {
        try {
            double distance = distanceCalculator.calculateDistance(
                    userLatitude, userLongitude,
                    fcmAppointmentRequestDTO.getTargetLatitude(),
                    fcmAppointmentRequestDTO.getTargetLongitude()
            );
            return distance <= 1.0; // 1km 이내
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.DISTANCE_CALCULATION_FAILED);
        }
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
        LocalDateTime startTime = LocalDateTime.now();

        for (int i = 0; i < 5; i++) {
            long delay = i + 1;
            try {
                scheduler.schedule(() -> {
                    // 경과 시간 계산
                    long minutesElapsed = Duration.between(startTime, LocalDateTime.now()).toMinutes();

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
