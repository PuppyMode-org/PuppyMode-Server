package umc.puppymode.service.AuthService;

import umc.puppymode.web.dto.AppleTokenResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

public interface AppleAuthCommandService {
    LoginResponseDTO loginWithApple(String authorizationCode, String identityToken, String username, String fcmToken);
    boolean verifyIdentityToken(String identityToken);
    AppleTokenResponseDTO getAppleTokens(String authorizationCode);
    String generateClientSecret();
}