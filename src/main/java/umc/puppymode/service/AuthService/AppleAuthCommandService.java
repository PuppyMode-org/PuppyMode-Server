package umc.puppymode.service.AuthService;

import umc.puppymode.web.dto.AppleTokenResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

public interface AppleAuthCommandService {
    LoginResponseDTO loginWithApple(String authorizationCode, String identityToken, String username, String fcmToken);

    LoginResponseDTO loginWithApple(String authorizationCode, String identityToken, String username, String email, String fcmToken);

    AppleTokenResponseDTO getAppleTokens(String authorizationCode);

    String generateClientSecret();
}