package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.puppymode.config.AppleAuthConfig;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Map;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthQueryServiceImpl implements AppleAuthQueryService {

    private final AppleKeyService appleKeyService;
    private final AppleAuthConfig appleAuthConfig;
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    /**
     * Identity Token 을 검증합니다.
     *
     * @param identityToken
     * @return Claims
     */
    public Claims verifyIdentityToken(String identityToken) {
        try {
//            log.info("검증할 Identity Token: {}", identityToken);

            // Identity Token JWT 헤더에서 kid 값 추출
            String kid = extractKidFromToken(identityToken);
//            log.info("Identity Token의 kid 값: {}", kid);

            // 공개 키 조회
            PublicKey publicKey = appleKeyService.getApplePublicKey(kid);
            if (publicKey == null) {
                log.error("공개 키를 찾을 수 없음 kid: {}", kid);
                throw new IllegalArgumentException("No matching public key found for kid: " + kid);
            }
//            log.info("Public Key 정상 조회됨 kid 매칭 성공. ");

            // Id Token JWT 검증 수행
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(10)
                    .build()
                    .parseClaimsJws(identityToken);

            Claims body = claims.getBody();
//            log.info("검증된 JWT Claims: {}", body);

            // iss(발급자) 검증
            if (!APPLE_ISSUER.equals(body.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer: " + body.getIssuer());
            }

            // aud(Audience) 검증
            if (!appleAuthConfig.getClientId().equals(body.getAudience())) {
                throw new IllegalArgumentException("Invalid audience: " + body.getAudience());
            }

            // exp(만료 시간) 검증
            if (body.getExpiration().toInstant().isBefore(Instant.now())) {
                throw new IllegalArgumentException("Token has expired");
            }

            return body;
        } catch (IllegalArgumentException e) {
            log.error("Identity Token 검증 실패: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Identity Token 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid Identity Token", e);
        }
    }

    /**
     * Id Token JWT 헤더에서 kid 값을 추출합니다.
     */
    public String extractKidFromToken(String identityToken) {
        try {
            String[] tokenParts = identityToken.split("\\.");
            if (tokenParts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format: " + identityToken);
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
//            log.info("JWT Header JSON: {}", headerJson);

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> headerMap = objectMapper.readValue(headerJson, Map.class);

            // kid 값 검증
            if (!headerMap.containsKey("kid")) {
                throw new IllegalArgumentException("JWT header does not contain 'kid' field");
            }

            String kid = headerMap.get("kid").toString();
//            log.info("Extracted kid from token: {}", kid);
            return kid;
        } catch (IllegalArgumentException e) {
            log.error("JWT Token Format Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract kid from token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to extract kid from token", e);
        }
    }
}
