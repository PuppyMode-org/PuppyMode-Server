package umc.puppymode.serivce.AuthService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umc.puppymode.config.AppleAuthConfig;
import org.springframework.web.reactive.function.client.WebClient;
import umc.puppymode.service.AuthService.AppleKeyServiceImpl;

public class AppleKeyServiceTest {

    private AppleKeyServiceImpl keyService;

    @BeforeEach
    public void setUp() {
        WebClient webClientMock = mock(WebClient.class);
        AppleAuthConfig configMock = mock(AppleAuthConfig.class);

        when(configMock.getPrivateKeyPath()).thenReturn("/Users/jisu/Documents/umc/7th_puppy_mode/AuthKey.p8"); // 가짜 경로 설정

        keyService = new AppleKeyServiceImpl(webClientMock, configMock);
    }

    @Test
    public void testLoadPrivateKey() {
        assertNotNull(keyService.loadPrivateKey(), "Private Key가 null이면 안 됩니다.");
        System.out.println("Private Key 로드 테스트 성공!");
    }
}