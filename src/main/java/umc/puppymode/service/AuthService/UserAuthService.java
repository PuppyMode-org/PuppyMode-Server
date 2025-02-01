package umc.puppymode.service.AuthService;

import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

public interface UserAuthService {
    LoginResponseDTO createOrUpdateUser(UserAuthInfoDTO userInfo, AuthProvider authProvider);

    Long getCurrentUserId();

    LoginResponseDTO loginWithFcmToken(LoginResponseDTO loginResponseDTO, String fcmToken);

    void saveFcmToken(Long userId, String fcmToken);
}
