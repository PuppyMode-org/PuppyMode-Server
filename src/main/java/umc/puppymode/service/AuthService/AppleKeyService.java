package umc.puppymode.service.AuthService;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface AppleKeyService {
    PublicKey getApplePublicKey(String identityToken);

    void refreshKeys();

    PrivateKey getPrivateKey();

    PrivateKey loadPrivateKey();
}
