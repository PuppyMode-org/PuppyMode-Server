package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

public interface UserAuthService {
    LoginResponseDTO createOrUpdateUser(KakaoUserInfoResponseDTO userInfo);
    Long getCurrentUserId();
    LoginResponseDTO loginWithFcmToken(LoginResponseDTO loginResponseDTO, String fcmToken);
    void saveFcmToken(Long userId, String fcmToken);
}
