package umc.puppymode.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@NoArgsConstructor
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

    @Value("${auth.apple.redirect-uri:https://puppy-mode.site/auth/apple/login}")
    private String redirectUri;
}