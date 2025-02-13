package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import umc.puppymode.config.AppleAuthConfig;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.web.dto.AppleTokenResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthCommandServiceImpl implements AppleAuthCommandService {

    private final RestTemplate restTemplate;
    private final AppleKeyService appleKeyService;
    private final AppleAuthConfig appleAuthConfig;
    private final AppleAuthQueryService appleAuthQueryService;
    private final UserAuthService userAuthService;

    private static final String APPLE_ISSUER = "https://appleid.apple.com";
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
     * Apple login을 진행합니다.
     *
     * @param authorizationCode
     * @param identityToken
     * @param fcmToken
     * @return 로그인 응답
     */
    @Override
    public LoginResponseDTO loginWithApple(String authorizationCode, String identityToken, String fcmToken) {
        UserAuthInfoDTO userInfo = appleAuthQueryService.getUserInfo(identityToken);

        AppleTokenResponseDTO appleTokens = getAppleTokens(authorizationCode);
        String refreshToken = appleTokens.getRefreshToken();

        LoginResponseDTO loginResponse = userAuthService.createOrUpdateUser(userInfo, AuthProvider.APPLE, refreshToken);

        if (fcmToken != null) {
            loginResponse = userAuthService.loginWithFcmToken(loginResponse, fcmToken);
        }

        return loginResponse;
    }

    /**
     * Identity Token을 검증합니다.
     */
    public boolean verifyIdentityToken(String identityToken) {
        try {
            // Identity Token JWT 헤더에서 kid 값 추출
            String kid = extractKidFromToken(identityToken);

            // 공개 키 조회
            PublicKey publicKey = appleKeyService.getApplePublicKey(kid);
            if (publicKey == null) {
                throw new IllegalArgumentException("No matching public key found for kid: " + kid);
            }

            // Id Token JWT 검증 수행
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken);

            Claims body = claims.getBody();

            // iss(발급자) 검증
            if (!APPLE_ISSUER.equals(body.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer: " + body.getIssuer());
            }

            // aud(Audience) 검증
            if (!clientId.equals(body.getAudience())) {
                throw new IllegalArgumentException("Invalid audience: " + body.getAudience());
            }

            // exp(만료 시간) 검증
            if (body.getExpiration().before(new Date())) {
                throw new IllegalArgumentException("Token has expired");
            }

            // sub(사용자 고유 ID) 반환
            String appleUserId = body.getSubject();
            log.info("Apple User ID: {}", appleUserId);

            return true;

        } catch (Exception e) {
            log.error("Identity Token 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 헤더에서 kid 값을 추출합니다.
     */
    private String extractKidFromToken(String identityToken) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(identityToken)
                .getHeader()
                .get("kid")
                .toString();
    }

    /**
     * Authorization Code를 사용하여 Access Token, Refresh Token 요청
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

        JsonNode response = restTemplate.postForObject(APPLE_TOKEN_URL, requestParams, JsonNode.class);

        if (Objects.isNull(response) || !response.has("access_token")) {
            log.error("Apple OAuth 토큰 요청 실패: 응답 없음");
            throw new RuntimeException("Failed to retrieve Apple tokens");
        }

        log.info("애플 Access Token 발급 완료");

        return new AppleTokenResponseDTO(
                response.get("access_token").asText(),
                response.get("refresh_token").asText()
        );
    }

    /**
     * Client Secret 생성
     */
    public String generateClientSecret() {
        try {
            long now = System.currentTimeMillis();
            long exp = now + (15777000 * 1000L); // 약 6개월 후 만료

            PrivateKey privateKey = appleKeyService.loadPrivateKey();

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