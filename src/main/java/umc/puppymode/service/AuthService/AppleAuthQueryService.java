package umc.puppymode.service.AuthService;

import umc.puppymode.web.dto.UserAuthInfoDTO;

public interface AppleAuthQueryService {
    UserAuthInfoDTO getUserInfo(String identityToken);
}