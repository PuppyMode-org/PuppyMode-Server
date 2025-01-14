package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.FcmService.FcmScheduledService;
import umc.puppymode.service.FcmService.FcmScheduledServiceImpl;
import umc.puppymode.service.FcmService.FcmService;
import umc.puppymode.web.dto.FCMRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

@Slf4j
@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;
    private final FcmScheduledService fcmScheduledService;

    public FcmController(FcmService fcmService, FcmScheduledService fcmScheduledService) {
        this.fcmService = fcmService;
        this.fcmScheduledService = fcmScheduledService;
    }

    @PostMapping("/send")
    @Operation(summary = "푸시 알림 즉시 전송 API", description = "요청 본문에 포함된 초기 푸시 알림을 즉시 전송합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendPushNotification(@RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmService.sendMessageTo(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule")
    @Operation(summary = "특정 시간 푸시 알림 전송 API", description = "요청 본문에 포함된 초기 푸시 알림 정보를 바탕으로 즉시 전송하고, 이후 설정된 시간에 정기적으로 푸시 알림을 전송합니다.")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> schedulePushNotification(@RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmScheduledService.schedulePushAtSpecificTime(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }
}
