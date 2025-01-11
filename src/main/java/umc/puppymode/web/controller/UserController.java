package umc.puppymode.web.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.UserService.UserCommandService;
import umc.puppymode.service.UserService.UserQueryService;
import umc.puppymode.web.dto.UserRequestDTO;
import umc.puppymode.web.dto.UserResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @GetMapping("/notifications")
    @Operation(summary = "알림 수신 여부 조회 API", description = "사용자의 알림 수신 여부를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getNotificationPreference() {
        Long userId = 1L;
        UserResponseDTO responseDto = userQueryService.getUserNotificationStatus(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    @PatchMapping("/notifications")
    @Operation(summary = "알림 수신 여부 수정 API", description = "사용자의 알림 수신 여부를 수정하는 API입니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateNotificationPreference(
            @RequestBody UserRequestDTO requestDto) {

        Long userId = 1L;
        userCommandService.updateUserNotificationStatus(userId, requestDto);
        UserResponseDTO responseDto = new UserResponseDTO(requestDto.isReceiveNotifications());
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

}
