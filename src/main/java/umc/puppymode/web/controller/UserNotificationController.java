package umc.puppymode.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.NotificationService.UserNotificationQueryService;
import umc.puppymode.web.dto.UserNotificationResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class UserNotificationController {

    private final UserNotificationQueryService userNotificationQueryService;

    @GetMapping("/status")
    public ApiResponse<List<UserNotificationResponseDTO>> getUserNotificationStatus() {
        Long userId = 1L;
        List<UserNotificationResponseDTO> response = userNotificationQueryService.getUserNotificationStatus(userId);
        return ApiResponse.onSuccess(response);
    }
}
