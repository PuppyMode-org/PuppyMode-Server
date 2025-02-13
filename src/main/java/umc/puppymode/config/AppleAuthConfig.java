package umc.puppymode.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Getter
@Component
public class AppleAuthConfig {

    @Value("${auth.apple.team-id}")
    private String teamId;

    @Value("${auth.apple.client-id}")
    private String clientId;

    @Value("${auth.apple.key-id}")
    private String keyId;

    @Value("${auth.apple.private-key-path}")
    private String privateKeyPath;

    private PrivateKey privateKey;

    public AppleAuthConfig(@Value("${auth.apple.private-key-path}") String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
        this.privateKey = loadPrivateKey();
    }

    private PrivateKey loadPrivateKey() {
        try {
            byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
            String privateKeyPEM = new String(keyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            log.error("Apple Private Key 로드 실패", e);
            throw new RuntimeException("Failed to load Apple Private Key", e);
        }
    }
}