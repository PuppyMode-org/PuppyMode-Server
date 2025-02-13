package umc.puppymode.service.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.web.dto.UserAuthInfoDTO;

import java.security.PublicKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthQueryServiceImpl implements AppleAuthQueryService {

    private final AppleKeyService appleKeyService;

    /**
     * Identity Token 검증 및 사용자 정보 추출
     *
     * @param identityToken
     * @return UserAuthInfoDTO
     */
    @Override
    public UserAuthInfoDTO getUserInfo(String identityToken) {
        try {
            PublicKey publicKey = appleKeyService.getApplePublicKey(identityToken);
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken);

            Claims payload = claims.getBody();
            String userAuthId = payload.get("sub", String.class);
            String email = payload.get("email", String.class);
            Boolean emailVerified = Boolean.parseBoolean(payload.get("email_verified", String.class));

            log.info("Apple Identity Token 검증 완료: sub={}, email={}, emailVerified={}", userAuthId, email, emailVerified);

            return UserAuthInfoDTO.builder()
                    .userAuthId(userAuthId)
                    .email(email)
                    .authProvider(AuthProvider.APPLE)
                    .build();
        } catch (JwtException e) {
            log.error("Invalid Apple Identity Token", e);
            throw new IllegalArgumentException("Invalid Apple Identity Token");
        }
    }
}
