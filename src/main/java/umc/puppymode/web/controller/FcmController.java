package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.FcmService.FcmAppointmentService;
import umc.puppymode.service.FcmService.FcmPlaytimeService;
import umc.puppymode.service.FcmService.FcmService;
import umc.puppymode.web.dto.FCMDTO.*;

@Slf4j
@RestController
@RequestMapping("/fcm/notifications")
public class FcmController {

    private final FcmService fcmService;
    private final FcmPlaytimeService fcmPlaytimeService;
    private final FcmAppointmentService fcmAppointmentService;

    public FcmController(FcmService fcmService, FcmPlaytimeService fcmPlaytimeService, FcmAppointmentService fcmAppointmentService) {
        this.fcmService = fcmService;
        this.fcmPlaytimeService = fcmPlaytimeService;
        this.fcmAppointmentService = fcmAppointmentService;
    }

    @PostMapping("/")
    @Operation(summary = "푸시 알림 API", description = "요청 본문에 포함된 푸시 알림을 즉시 전송합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendPushNotification(
            @RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmService.sendMessageTo(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/playtimes")
    @Operation(summary = "놀아주기 푸시 알림 전송 API", description = "매일 정해진 시간(오전 10시)에 푸시 알림이 전송됩니다.")
    public ResponseEntity<ApiResponse<FCMPlayResponseDTO>> schedulePushNotification() {

        ApiResponse<FCMPlayResponseDTO> response = fcmPlaytimeService.schedulePlaytimeNotification();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments")
    @Operation(summary = "술 약속 푸시 알림 API", description = "진행 중인 약속에 대해 첫 알림을 전송하고 이후 1분 간격으로 랜덤 푸시 알림을 전송합니다.")
    public ResponseEntity<ApiResponse<FCMAppointmentResponseDTO>> sendDrinkingScheduleNotification() {

        ApiResponse<FCMAppointmentResponseDTO> response = fcmAppointmentService.scheduleDrinkingNotifications();
        return ResponseEntity.ok(response);
    }
}
