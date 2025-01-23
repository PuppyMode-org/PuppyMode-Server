package umc.puppymode.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.AuthService.KakaoService;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserAuthService userAuthService;

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> callback(@RequestParam("code") String code) {
        try {
            // 카카오에서 Access Token 가져오기
            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            log.info("Received authorization code: {}", code);

            // 사용자 정보 가져오기
            KakaoUserInfoResponseDTO userInfo = kakaoService.getUserInfo(accessToken);

            // 사용자 정보 유효성 검사
            if (userInfo.getKakaoAccount().getProfile().getNickName() == null) {
                throw new IllegalArgumentException("닉네임이 존재하지 않습니다.");
            }

            if (userInfo.getKakaoAccount().getEmail() == null) {
                throw new IllegalArgumentException("이메일이 존재하지 않습니다.");
            }

            // 로그인 또는 회원가입 처리
            LoginResponseDTO loginResponse = userAuthService.createOrUpdateUser(userInfo);

            // 성공 응답 반환
            return ResponseEntity.ok(ApiResponse.onSuccess(loginResponse));
        } catch (Exception e) {
            log.error("카카오 로그인 오류 발생: {}", e.getMessage(), e);

            // 실패 응답 반환
            ApiResponse<LoginResponseDTO> errorResponse = ApiResponse.onFailure(
                    "AUTH_ERROR",
                    "카카오 로그인 오류가 발생했습니다.",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}