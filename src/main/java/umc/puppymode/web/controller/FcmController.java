package umc.puppymode.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.FcmService.FcmService;
import umc.puppymode.web.dto.FCMRequestDTO;
import umc.puppymode.web.dto.FCMResponseDTO;

@Slf4j
@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<FCMResponseDTO>> sendPushNotification(@RequestBody FCMRequestDTO fcmRequestDTO) {

        ApiResponse<FCMResponseDTO> response = fcmService.sendMessageTo(fcmRequestDTO);
        return ResponseEntity.ok(response);
    }
}
