package umc.puppymode.web.controller;

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
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendPushNotification(@RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmService.sendMessageTo(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> schedulePushNotification(@RequestBody FCMRequestDTO fcmRequestDTO) {
        log.info("예약된 푸시 알림 요청: {}", fcmRequestDTO);
        ApiResponse<FCMResponseDTO> response = fcmScheduledService.schedulePushAtSpecificTime(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }
}
