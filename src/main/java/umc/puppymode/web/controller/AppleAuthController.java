package umc.puppymode.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.AuthService.AppleAuthCommandService;
import umc.puppymode.web.dto.AppleLoginRequestDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/apple")
public class AppleAuthController {

    private final AppleAuthCommandService appleAuthCommandService;
    private final ObjectMapper objectMapper;

    /**
     * iOS 클라이언트용 애플 로그인 엔드포인트입니다.
     * JSON 요청을 받습니다.
     */
//    @PostMapping("/login")
    @Operation(summary = "애플 로그인 API",
            description = "애플 서버로부터 발급받은 `Authorization Token`과 `Identity Token`을 사용하여,  \n" +
                    "서버에서 JWT를 발급받는 API입니다.  \n" +
                    "로그인 및 회원가입 처리를 포함합니다.  \n" +
                    "`FCMToken`을 함께 전송하여 푸시 알림을 위한 토큰을 저장합니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> appleLogin(@RequestBody AppleLoginRequestDTO request) {
        try {
            String username = request.getUsername();

            return ResponseEntity.ok(ApiResponse.onSuccess(
                    processAppleLogin(
                            request.getAuthorizationCode(),
                            request.getIdentityToken(),
                            username,
                            request.getFcmToken())
            ));
        } catch (IllegalArgumentException e) {
            log.error("애플 로그인 유효성 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("VALIDATION_ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("애플 로그인 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("AUTH_ERROR", "애플 로그인 오류가 발생했습니다.", null));
        }
    }

    /**
     * 웹 테스트 용 애플 로그인 엔드포인트입니다.
     * form_post 요청을 받습니다.
     */
//    @PostMapping("/web/callback")
    @PostMapping("/login")
    @Operation(summary = "웹 테스트용 애플 로그인 API",
            description = "웹 방식으로 애플 로그인을 테스트합니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> appleWebLogin(
            @RequestParam Map<String, String> formParams) {
        try {
            log.debug("웹 애플 로그인 요청: {}", formParams);

            if (!formParams.containsKey("code") || !formParams.containsKey("id_token")) {
                log.warn("애플 로그인 요청 누락된 필수 값: {}", formParams);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure("VALIDATION_ERROR", "code 또는 id_token이 누락되었습니다.", null));
            }

            String authorizationCode = formParams.get("code");
            String identityToken = formParams.get("id_token");
            String userJson = formParams.get("user");
            String fcmToken = formParams.getOrDefault("fcm_token", null);

            String username = extractUsername(userJson);

            return ResponseEntity.ok(ApiResponse.onSuccess(
                    processAppleLogin(authorizationCode, identityToken, username, fcmToken)
            ));
        } catch (IllegalArgumentException e) {
            log.warn("애플 로그인 유효성 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("VALIDATION_ERROR", e.getMessage(), null));
        } catch (Exception e) {
            log.error("애플 로그인 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("AUTH_ERROR", "애플 로그인 오류가 발생했습니다.", null));
        }
    }

    /**
     * 애플 로그인 처리 공통 로직입니다.
     */
    private LoginResponseDTO processAppleLogin(String authorizationCode, String identityToken, String username, String fcmToken) {
        return appleAuthCommandService.loginWithApple(authorizationCode, identityToken, username, fcmToken);
    }

    /**
     * JSON에서 username(firstName + lastName)을 추출합니다.
     *
     * @param userJson
     * @return username
     */
    private String extractUsername(String userJson) {
        if (userJson == null || userJson.isEmpty()) {
            return null;
        }

        try {
            JsonNode userNode = objectMapper.readTree(userJson);
            JsonNode nameNode = userNode.get("name");
            if (nameNode == null) {
                return null;
            }

            String firstName = nameNode.has("firstName") ? nameNode.get("firstName").asText() : "";
            String lastName = nameNode.has("lastName") ? nameNode.get("lastName").asText() : "";

            if (firstName.isEmpty() && lastName.isEmpty()) {
                return null;
            }

            return (firstName + " " + lastName).trim();
        } catch (Exception e) {
            log.error("애플 로그인 사용자 이름 파싱 실패", e);
            return null;
        }
    }
}
