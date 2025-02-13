package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import umc.puppymode.config.AppleAuthConfig;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static umc.puppymode.service.AuthService.ApplePublicKeyUtil.generatePublicKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleKeyServiceImpl implements AppleKeyService {

    private final WebClient webClient;
    private final AppleAuthConfig appleAuthConfig;
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final Map<String, PublicKey> publicKeyCache = new HashMap<>();
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        refreshKeys();
        this.privateKey = loadPrivateKey();
    }

    /**
     * 공개 키를 조회합니다.
     * Identity Token 검증용
     */
    @Override
    public PublicKey getApplePublicKey(String kid) {
        return publicKeyCache.get(kid);
    }

    /**
     * 6시간마다 공개 키를 갱신합니다.
     */
    @Override
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void refreshKeys() {
        try {
            log.info("애플 공개 키 갱신 시작...");
            JsonNode response = webClient.get()
                    .uri(APPLE_KEYS_URL)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || !response.has("keys")) {
                log.error("애플 공개 키 응답 없음");
                throw new IllegalArgumentException("Apple public keys response is empty");
            }

            Map<String, PublicKey> newKeyCache = new HashMap<>();
            for (JsonNode key : response.get("keys")) {
                String kid = key.get("kid").asText();
                String n = key.get("n").asText();
                String e = key.get("e").asText();

                PublicKey publicKey = generatePublicKey(n, e);
                newKeyCache.put(kid, publicKey);
            }

            publicKeyCache.clear();
            publicKeyCache.putAll(newKeyCache);
            log.info("애플 공개 키 갱신 완료. 저장된 키 개수: {}", publicKeyCache.size());

        } catch (Exception e) {
            log.error("애플 공개 키 갱신 실패: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to refresh Apple public keys", e);
        }
    }

    /**
     * Private Key를 로드합니다.
     */
    public PrivateKey loadPrivateKey() {
        try {
            String privateKeyPath = appleAuthConfig.getPrivateKeyPath();
            log.info("Private Key 파일 경로: {}", privateKeyPath);
            byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
            String privateKeyPEM = new String(keyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            log.info("Private Key (Base64): {}", privateKeyPEM);

            byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Apple Private Key 로드 실패", e);
        }
    }

    /**
     * Private Key를 반환합니다.
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
}