package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.config.security.JwtTokenProvider;
import umc.puppymode.domain.User;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.service.UserService.UserCommandService;
import umc.puppymode.service.UserService.UserInfoService;
import umc.puppymode.service.UserService.UserQueryService;
import umc.puppymode.web.dto.UserInfoDTO;
import umc.puppymode.web.dto.UserRequestDTO;
import umc.puppymode.web.dto.UserResponseDTO;

import java.util.IllformedLocaleException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserAuthService userAuthService;
    private final UserInfoService userInfoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @GetMapping("/notifications")
    @Operation(summary = "알림 수신 여부 조회 API", description = "사용자의 알림 수신 여부를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getNotificationPreference() {

        Long userId = userAuthService.getCurrentUserId();

        UserResponseDTO responseDTO = userQueryService.getUserNotificationStatus(userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @PatchMapping("/notifications")
    @Operation(summary = "알림 수신 여부 수정 API", description = "사용자의 알림 수신 여부를 수정하는 API입니다.")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateNotificationPreference(
            @RequestBody UserRequestDTO requestDTO) {

        Long userId = userAuthService.getCurrentUserId();

        userCommandService.updateUserNotificationStatus(userId, requestDTO);

        UserResponseDTO responseDto = new UserResponseDTO(requestDTO.isReceiveNotifications());

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDto));
    }

    @GetMapping("")
    @Operation(summary = "사용자 정보 조회 API", description = "사용자의 정보를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<UserInfoDTO>> getUserInfo() {
        // userId 추출
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(principal);

        UserInfoDTO userInfoDTO = userInfoService.getUserInfo(userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(userInfoDTO));
    }
}