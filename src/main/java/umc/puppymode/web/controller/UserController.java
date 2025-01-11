package umc.puppymode.web.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.UserService.UserQueryService;
import umc.puppymode.web.dto.UserNotificationResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/notifications")
    @Operation(summary = "알림 수신 여부 조회 API", description = "사용자의 알림 수신 여부를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<UserNotificationResponseDTO>> getNotificationPreference() {
        Long userId = 1L;
        UserNotificationResponseDTO responseDto = userQueryService.getUserNotificationStatus(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }
}
