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

    @Value("${auth.apple.private-key}")
    private String privateKey;
}