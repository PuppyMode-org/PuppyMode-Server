package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import umc.puppymode.config.AppleAuthConfig;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.web.dto.AppleTokenResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

import java.security.PrivateKey;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthCommandServiceImpl implements AppleAuthCommandService {

    private final WebClient webClient;
    private final AppleKeyService appleKeyService;
    private final AppleAuthConfig appleAuthConfig;
    private final AppleAuthQueryService appleAuthQueryService;
    private final UserAuthService userAuthService;

    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";
    private String clientId;
    private String keyId;
    private String teamId;

    @PostConstruct
    public void init() {
        this.clientId = appleAuthConfig.getClientId();
        this.keyId = appleAuthConfig.getKeyId();
        this.teamId = appleAuthConfig.getTeamId();
    }

    /**
     * Apple Login 을 처리합니다.
     *
     * @param authorizationCode
     * @param identityToken
     * @param fcmToken
     * @return 로그인 응답
     */
    @Override
    public LoginResponseDTO loginWithApple(String authorizationCode, String identityToken, String username, String fcmToken) {

        Claims claims = appleAuthQueryService.verifyIdentityToken(identityToken);
        if (claims == null) {
            throw new IllegalArgumentException("Invalid identity token");
        }

        UserAuthInfoDTO userInfo = UserAuthInfoDTO.builder()
                .userAuthId(claims.getSubject())
                .email(claims.get("email", String.class))
                .authProvider(AuthProvider.APPLE)
                .build();

        if (username != null && !username.isEmpty()) {
            userInfo = UserAuthInfoDTO.builder()
                    .userAuthId(userInfo.getUserAuthId())
                    .userId(userInfo.getUserId())
                    .email(userInfo.getEmail())
                    .authProvider(userInfo.getAuthProvider())
                    .username(username)
                    .build();
        }

        AppleTokenResponseDTO appleTokens = getAppleTokens(authorizationCode);
        String refreshToken = appleTokens.getRefreshToken();

        LoginResponseDTO loginResponse = userAuthService.createOrUpdateUser(userInfo, AuthProvider.APPLE, refreshToken);

        if (fcmToken != null) {
            loginResponse = userAuthService.loginWithFcmToken(loginResponse, fcmToken);
        }

        return loginResponse;
    }

    /**
     * Authorization Code 를 사용하여 Access Token, Refresh Token 을 요청합니다.
     *
     * @param authorizationCode
     * @return AppleTokenResponseDTO
     */
    @Override
    public AppleTokenResponseDTO getAppleTokens(String authorizationCode) {
        String clientSecret = generateClientSecret();

        Map<String, String> requestParams = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", authorizationCode,
                "grant_type", "authorization_code",
                "redirect_uri", "https://puppy-mode.site/auth/apple/login"
        );

        JsonNode response = webClient.post()
                .uri(APPLE_TOKEN_URL)
                .bodyValue(requestParams)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (Objects.isNull(response) || !response.has("access_token")) {
            log.error("Apple Access Token 요청 실패: 응답 없음");
            throw new RuntimeException("Failed to retrieve Apple tokens");
        }

        log.info("애플 Access Token 발급 완료");

        return new AppleTokenResponseDTO(
                response.get("access_token").asText(),
                response.get("refresh_token").asText()
        );
    }

    /**
     * Client Secret 을 생성합니다.
     */
    public String generateClientSecret() {
        try {
            long now = System.currentTimeMillis();
            long exp = now + (15777000 * 1000L); // 약 6개월 후 만료

            PrivateKey privateKey = appleKeyService.getPrivateKey();

            return Jwts.builder()
                    .setHeaderParam("alg", "ES256")
                    .setHeaderParam("kid", keyId)
                    .setIssuer(teamId)
                    .setIssuedAt(new java.util.Date(now))
                    .setExpiration(new java.util.Date(exp))
                    .setAudience("https://appleid.apple.com")
                    .setSubject(clientId)
                    .signWith(privateKey, io.jsonwebtoken.SignatureAlgorithm.ES256)
                    .compact();

        } catch (Exception e) {
            log.error("애플 Client Secret 생성 실패", e);
            throw new RuntimeException("Failed to generate Apple Client Secret", e);
        }
    }
}