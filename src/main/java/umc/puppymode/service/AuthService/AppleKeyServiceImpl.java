package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import umc.puppymode.config.AppleAuthConfig;

import javax.annotation.PostConstruct;
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
    public final Map<String, PublicKey> publicKeyCache = new HashMap<>();
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        refreshKeys();
//        log.info("AppleKeyServiceImpl 초기화, Private Key 로드...");
        loadPrivateKeyAndStore();
    }

    private void loadPrivateKeyAndStore() {
        this.privateKey = loadPrivateKey();
//        log.info("Private Key 설정 완료: {}", this.privateKey != null ? "성공" : "실패");
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
//            log.info("애플 공개 키 갱신 시작...");
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
//            log.info("애플 공개 키 갱신 완료. 저장된 키 개수: {}", publicKeyCache.size());
//            log.info("Apple 공개 키 캐싱: {}", publicKeyCache.keySet());

        } catch (Exception e) {
            log.error("애플 공개 키 갱신 실패: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to refresh Apple public keys", e);
        }
    }

    /**
     * Apple Private Key를 로드합니다.
     * 1. 환경 변수에서 Base64로 인코딩된 키를 로드
     * 2. Base64 디코딩하여 PEM 형식의 헤더 및 푸터 제거
     * 3. 다시 Base64 디코딩하여 PKCS8EncodedKeySpec 변환 후 PrivateKey 반환
     */
    public PrivateKey loadPrivateKey() {
        try {
            String privateKeyBase64 = appleAuthConfig.getPrivateKey();

            if (privateKeyBase64 == null || privateKeyBase64.isEmpty()) {
                privateKeyBase64 = System.getenv("APPLE_PRIVATE_KEY_BASE64");
            }

            if (privateKeyBase64 == null || privateKeyBase64.isEmpty()) {
                privateKeyBase64 = System.getProperty("APPLE_PRIVATE_KEY_BASE64");
            }

//            log.info("APPLE_PRIVATE_KEY_BASE64 설정 상태: {}", privateKeyBase64 != null ? "설정됨" : "설정되지 않음");
//            log.info("APPLE_PRIVATE_KEY_BASE64: {}", privateKeyBase64);
            if (privateKeyBase64 == null || privateKeyBase64.isEmpty()) {
                throw new IllegalArgumentException("Apple Private Key 값이 설정되지 않았습니다. 환경 변수를 확인하세요.");
            }

            // Base64 디코딩 (헤더/푸터 포함된 PEM 형식)
            byte[] pemBytes;
            try {
                pemBytes = Base64.getDecoder().decode(privateKeyBase64);
//                log.info("Apple Private Key Base64 디코딩 성공 (길이: {} 바이트)", pemBytes.length);
            } catch (IllegalArgumentException e) {
//                log.error("Apple Private Key Base64 디코딩 실패: 환경 변수 값이 잘못되었습니다.");
                throw e;
            }

            // PEM 헤더 및 푸터 제거
            String pemContent = stripPemHeaders(new String(pemBytes));
//            log.info("Apple Private Key PEM 헤더 제거 완료 (길이: {} 바이트)", pemContent.length());
//            log.info("Apple Private Key PEM 헤더 제거 완료 {}", pemContent);

            // 다시 Base64 디코딩하여 바이너리 키 데이터로 변환
            byte[] keyBytes;
            try {
                keyBytes = Base64.getDecoder().decode(pemContent);
//                log.info("Apple Private Key 최종 디코딩 성공 (길이: {} 바이트)", keyBytes.length);
            } catch (IllegalArgumentException e) {
//                log.error("Apple Private Key 최종 Base64 디코딩 실패: 잘못된 포맷입니다.");
                throw e;
            }

            // PKCS8EncodedKeySpec 변환 및 PrivateKey 객체 생성
            try {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("EC"); // Apple의 AuthKey는 보통 EC (Elliptic Curve) 사용
                PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
//                log.info("Apple Private Key 로드 성공");
                return privateKey;
            } catch (Exception e) {
//                log.error("Apple Private Key 변환 실패: 잘못된 키 형식입니다.", e);
                throw new IllegalArgumentException("Apple Private Key 변환 실패: 잘못된 키 형식입니다.", e);
            }

        } catch (IllegalArgumentException e) {
            log.error("Apple Private Key 로드 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * PEM 헤더, 푸터를 제거합니다.
     */
    private String stripPemHeaders(String pem) {
        return pem.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // 공백 제거
    }

    /**
     * Private Key를 반환합니다.
     */
    public PrivateKey getPrivateKey() {
        if (this.privateKey == null) {
            log.warn("getPrivateKey() 호출됨, 그러나 privateKey가 설정되지 않았습니다.");
        } else {
//            log.info("getPrivateKey() 호출됨, privateKey가 정상적으로 설정됨.");
        }
        return this.privateKey;
    }
}