package umc.puppymode.service.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.puppymode.config.AppleAuthConfig;

import java.security.PublicKey;
import java.util.Date;

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
            if (!appleAuthConfig.getClientId().equals(body.getAudience())) {
                throw new IllegalArgumentException("Invalid audience: " + body.getAudience());
            }

            // exp(만료 시간) 검증
            if (body.getExpiration().before(new Date())) {
                throw new IllegalArgumentException("Token has expired");
            }

            return body;

        } catch (Exception e) {
            log.error("Identity Token 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid Identity Token");
        }
    }

    /**
     * Id Token JWT 헤더에서 kid 값을 추출합니다.
     */
    private String extractKidFromToken(String identityToken) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(identityToken)
                .getHeader()
                .get("kid")
                .toString();
    }
}
