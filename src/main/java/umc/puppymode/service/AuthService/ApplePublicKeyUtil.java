package umc.puppymode.service.AuthService;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class ApplePublicKeyUtil {

    private ApplePublicKeyUtil() {
        // 인스턴스 생성 방지
    }

    /**
     * RSA Public Key를 생성합니다.
     *
     * @param n 애플에서 제공받은 RSA Modulus
     * @param e Exponent
     * @return 실제 Public Key 객체
     */
    public static PublicKey generatePublicKey(String n, String e) {
        try {
            byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
            byte[] exponentBytes = Base64.getUrlDecoder().decode(e);

            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate Apple public key", ex);
        }
    }
}