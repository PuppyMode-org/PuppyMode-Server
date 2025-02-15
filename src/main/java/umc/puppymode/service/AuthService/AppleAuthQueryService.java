package umc.puppymode.service.AuthService;

import io.jsonwebtoken.Claims;
import umc.puppymode.web.dto.UserAuthInfoDTO;

public interface AppleAuthQueryService {
    Claims verifyIdentityToken(String identityToken);
    UserAuthInfoDTO getUserInfo(String identityToken);
}