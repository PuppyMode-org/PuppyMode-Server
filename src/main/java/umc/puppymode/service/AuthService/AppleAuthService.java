package umc.puppymode.service.AuthService;

import umc.puppymode.web.dto.UserAuthInfoDTO;

public interface AppleAuthService {
    UserAuthInfoDTO getUserInfo(String identityToken);
}
