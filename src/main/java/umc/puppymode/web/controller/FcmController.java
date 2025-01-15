package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.FcmService.FcmAppointmentService;
import umc.puppymode.service.FcmService.FcmPlaytimeService;
import umc.puppymode.service.FcmService.FcmService;
import umc.puppymode.web.dto.FCMAppointmentRequestDTO;
import umc.puppymode.web.dto.FCMPlayRequestDTO;
import umc.puppymode.web.dto.FCMRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

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
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendPushNotification(@RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmService.sendMessageTo(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/playtimes")
    @Operation(summary = "놀아주기 푸시 알림 전송 API", description = "요청 본문에 포함된 초기 푸시 알림 정보를 바탕으로 즉시 전송하고, 이후 설정된 시간에 정기적으로 푸시 알림을 전송합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> schedulePushNotification(@RequestBody FCMPlayRequestDTO fcmPlayRequestDTO ) {

        ApiResponse<FCMResponseDTO> response = fcmPlaytimeService.schedulePushAtSpecificTime(fcmPlayRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments")
    @Operation(summary = "술 약속 푸시 알림 API", description = "약속 시간 이후 약속 장소 반경 1km 이내 도착 시 최초 알림을 전송합니다. 이후 1시간 간격으로 랜덤 알림 5개를 제공합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendDrinkingScheduleNotification(
            @RequestBody FCMAppointmentRequestDTO fcmAppointmentRequestDTO
    ) {
        log.info("술 약속 푸시 알림 요청: {}", fcmAppointmentRequestDTO);

        ApiResponse<FCMResponseDTO> response = fcmAppointmentService.scheduleDrinkingNotifications(fcmAppointmentRequestDTO);
        return ResponseEntity.ok(response);
    }

}
