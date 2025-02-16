package umc.puppymode.service.AuthService;

import io.jsonwebtoken.Claims;

public interface AppleAuthQueryService {
    Claims verifyIdentityToken(String identityToken);
}