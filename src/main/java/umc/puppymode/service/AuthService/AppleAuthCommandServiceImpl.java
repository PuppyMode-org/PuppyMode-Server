package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import umc.puppymode.config.AppleAuthConfig;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.web.dto.AppleTokenResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

import java.security.PrivateKey;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthCommandServiceImpl implements AppleAuthCommandService {

    private final WebClient webClient;
    private final AppleKeyService appleKeyService;
    private final AppleAuthQueryService appleAuthQueryService;
    private final UserAuthService userAuthService;
    private final AppleAuthConfig appleAuthConfig;

    private static final String APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token";

    @PostConstruct
    public void init() {
//        log.info("PostConstruct - AppleAuthCommandServiceImpl @PostConstruct 생성됨");

        if (appleAuthConfig == null) {
            throw new IllegalStateException("AppleAuthConfig가 주입되지 않았습니다.");
        }

//        log.info("PostConstruct - AppleAuthConfig 초기화 상태: clientId={}, keyId={}, teamId={}",
//                appleAuthConfig.getClientId(), appleAuthConfig.getKeyId(), appleAuthConfig.getTeamId());

        if (appleAuthConfig.getClientId() == null || appleAuthConfig.getKeyId() == null || appleAuthConfig.getTeamId() == null) {
            throw new IllegalStateException("AppleAuthCommandServiceImpl - AppleAuthConfig에서 clientId, keyId, teamId가 올바르게 설정되지 않았습니다.");
        }
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
    public LoginResponseDTO loginWithApple(String authorizationCode, String identityToken,
                                           String username, String fcmToken) {
        return loginWithApple(authorizationCode, identityToken, username, null, fcmToken);
    }

    @Override
    public LoginResponseDTO loginWithApple(String authorizationCode, String identityToken, String username, String email, String fcmToken) {

        Claims claims = appleAuthQueryService.verifyIdentityToken(identityToken);
        if (claims == null) {
            throw new IllegalArgumentException("Invalid identity token");
        }

        if (email == null) {
            email = claims.get("email", String.class);
        }

        UserAuthInfoDTO userInfo = UserAuthInfoDTO.builder()
                .userAuthId(claims.getSubject())
                .email(email)
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
        if (authorizationCode == null || authorizationCode.trim().isEmpty()) {
            log.error("Apple Access Token 요청 실패: Authorization Code가 비어 있습니다.");
            throw new IllegalArgumentException("Authorization Code가 비어 있습니다.");
        }

        String clientSecret;
        try {
            clientSecret = generateClientSecret();
//            log.info("Client Secret 생성 완료");
        } catch (Exception e) {
            log.error("Client Secret 생성 중 오류 발생" + e.getMessage(), e);
            throw new RuntimeException("Client Secret 생성 실패" + e.getMessage(), e);
        }

        String clientId = appleAuthConfig.getClientId();
        String redirectUri = appleAuthConfig.getRedirectUri();

//        log.info("Apple Access Token 요청: client_id={}, code={}, grant_type={}, redirect_uri={}",
//                clientId, authorizationCode.substring(0, 5), "authorization_code", redirectUri);

        JsonNode response;
        try {
            response = webClient.post()
                    .uri(APPLE_TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("code", authorizationCode)
                            .with("grant_type", "authorization_code")
                            .with("redirect_uri", redirectUri))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        log.error("Apple API 요청 오류 (4xx): HTTP 상태 코드 {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("Apple API 응답 바디 (4xx): {}", errorBody))
                                .flatMap(errorBody -> Mono.error(new IllegalArgumentException("Apple API 요청 실패 (4xx): " + errorBody)));
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                        log.error("Apple API 서버 오류 (5xx): HTTP 상태 코드 {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .doOnNext(errorBody -> log.error("Apple API 응답 바디 (5xx): {}", errorBody))
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Apple API 서버 오류 (5xx): " + errorBody)));
                    })
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException 발생: HTTP 상태 코드={}, 응답 바디={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Apple API 요청 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Apple Access Token 요청 중 예외 발생" + e.getMessage(), e);
            throw new RuntimeException("Apple API 요청 실패" + e.getMessage(), e);
        }

        if (Objects.isNull(response) || !response.has("access_token")) {
            log.error("Apple Access Token 요청 실패: 응답 없음 또는 액세스 토큰 없음");
            throw new RuntimeException("Failed to retrieve Apple tokens");
        }
//        log.info("Apple Access Token 발급 완료 access{},\n refresh{}", response.get("access_token").asText(), response.get("refresh_token").asText());
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
            if (privateKey == null) {
                throw new IllegalStateException("Private Key가 null 입니다. 초기화 순서를 확인하세요.");
            }

            String clientId = appleAuthConfig.getClientId();
            String keyId = appleAuthConfig.getKeyId();
            String teamId = appleAuthConfig.getTeamId();

//            log.info("🔍 generateClientSecret - AppleAuthConfig 상태: clientId={}, keyId={}, teamId={}", clientId, keyId, teamId);

            if (clientId == null || keyId == null || teamId == null) {
//                log.info("generateClientSecret - clientId, keyId, teamId 중 하나가 null입니다. AppleAuthConfig 설정을 확인하세요.",
//                        appleAuthConfig.getClientId(), appleAuthConfig.getKeyId(), appleAuthConfig.getTeamId());
                throw new IllegalStateException("generateClientSecret - clientId, keyId, teamId 중 하나가 null입니다. AppleAuthConfig 설정을 확인하세요.");
            }

            String clientSecret = Jwts.builder()
                    .setHeaderParam("alg", "ES256")
                    .setHeaderParam("kid", keyId)
                    .setIssuer(teamId)
                    .setIssuedAt(new java.util.Date(now))
                    .setExpiration(new java.util.Date(exp))
                    .setAudience("https://appleid.apple.com")
                    .setSubject(clientId)
                    .signWith(privateKey, io.jsonwebtoken.SignatureAlgorithm.ES256)
                    .compact();

//            log.info("generateClientSecret - Client Secret 생성 완료 {}", clientSecret);

            return clientSecret;

        } catch (Exception e) {
            log.error("애플 Client Secret 생성 실패", e);
            throw new RuntimeException("Failed to generate Apple Client Secret", e);
        }
    }
}