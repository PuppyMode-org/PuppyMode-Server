package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

public interface UserAuthService {
    LoginResponseDTO createOrUpdateUser(KakaoUserInfoResponseDTO userInfo);
    Long getCurrentUserId();
}
