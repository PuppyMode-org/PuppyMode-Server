package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.FcmService.FcmAppointmentService;
import umc.puppymode.service.FcmService.FcmPlaytimeService;
import umc.puppymode.service.FcmService.FcmService;
import umc.puppymode.web.dto.*;

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
    @Operation(summary = "푸시 알림 즉시 전송 API", description = "요청 본문에 포함된 초기 푸시 알림을 즉시 전송합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendPushNotification(
            @RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmService.sendMessageTo(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/playtimes")
    @Operation(summary = "놀아주기 푸시 알림 전송 API", description = "매일 정해진 시간에 푸시 알림이 전송됩니다.")
    public ResponseEntity<ApiResponse<FCMPlayResponseDTO>> schedulePushNotification() {

        ApiResponse<FCMPlayResponseDTO> response = fcmPlaytimeService.schedulePushAtSpecificTime();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments")
    @Operation(summary = "술 약속 푸시 알림 API", description = "약속 시간 이후 약속 장소 반경 1km 이내 도착 시 최초 알림을 전송합니다. 이후 1시간 간격으로 랜덤 알림 5개를 제공합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendDrinkingScheduleNotification(
            @RequestBody FCMAppointmentRequestDTO fcmAppointmentRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmAppointmentService.scheduleDrinkingNotifications(fcmAppointmentRequestDTO);
        return ResponseEntity.ok(response);
    }
}
