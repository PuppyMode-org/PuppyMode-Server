package umc.puppymode.service.AuthService;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    /**
     * 카카오 회원의 정보를 가져옵니다.
     *
     * @param accessToken
     * @return 회원 정보 전체
     */
    public UserAuthInfoDTO getUserInfo(String accessToken) {

        KakaoUserInfoResponseDTO userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDTO.class)
                .block();

//        log.info("[ Kakao Service ] Auth ID —> {} ", userInfo.getId());
//        log.info("[ Kakao Service ] NickName —> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
//        log.info("[ Kakao Service ] email —> {} ", userInfo.getKakaoAccount().getEmail());

        validateKakaoUserInfo(userInfo);

        return UserAuthInfoDTO.builder()
                .userAuthId(userInfo.getId().toString())  // 카카오 고유 ID
                .email(userInfo.getKakaoAccount().getEmail())
                .username(userInfo.getKakaoAccount().getProfile().getNickName())
                .authProvider(AuthProvider.KAKAO)
                .build();
    }

    /**
     * 카카오 사용자 정보가 null 인지 검사합니다.
     */
    private void validateKakaoUserInfo(KakaoUserInfoResponseDTO userInfo) {
        if (userInfo == null || userInfo.getId() == null) {
            throw new IllegalArgumentException("카카오 계정의 고유 ID(auth_id)가 존재하지 않습니다.");
        }

        if (userInfo.getKakaoAccount() == null) {
            throw new IllegalArgumentException("카카오 계정 정보가 존재하지 않습니다.");
        }

        if (userInfo.getKakaoAccount().getProfile() == null ||
                userInfo.getKakaoAccount().getProfile().getNickName() == null) {
            throw new IllegalArgumentException("카카오 닉네임 정보가 존재하지 않습니다.");
        }

        if (userInfo.getKakaoAccount().getEmail() == null) {
            throw new IllegalArgumentException("카카오 이메일 정보가 존재하지 않습니다.");
        }
    }
}