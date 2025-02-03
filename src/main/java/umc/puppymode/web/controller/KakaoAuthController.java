package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.service.AuthService.KakaoAuthService;
import umc.puppymode.service.AuthService.UserAuthService;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserAuthService userAuthService;

    @GetMapping("/login")
    @Operation(summary = "카카오 로그인 API",
            description = "카카오 서버로부터 발급받은 `Access Token`과 `Refresh Token`을 사용하여,  \n" +
                    "서버에서 JWT를 발급받는 API입니다.  \n" +
                    "로그인 및 회원가입 처리를 포함합니다.  \n" +
                    "`FCMToken`을 함께 전송하여 푸시 알림을 위한 토큰을 저장합니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> kakaoLogin(
            @RequestParam("accessToken") String accessToken,
            @RequestParam(value = "refreshToken", required = false) String refreshToken,
            @RequestParam(value = "FCMToken", required = false) String fcmToken) {
        try {
            UserAuthInfoDTO userInfo = kakaoAuthService.getUserInfo(accessToken);
            LoginResponseDTO loginResponse = userAuthService.createOrUpdateUser(userInfo, AuthProvider.KAKAO);
            loginResponse = userAuthService.loginWithFcmToken(loginResponse, fcmToken);

            return ResponseEntity.ok(ApiResponse.onSuccess(loginResponse));
        } catch (IllegalArgumentException e) {
            log.error("카카오 로그인 유효성 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("VALIDATION_ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("카카오 로그인 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("AUTH_ERROR", "카카오 로그인 오류가 발생했습니다.", null));
        }
    }
}