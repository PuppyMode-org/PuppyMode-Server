package umc.puppymode.service.AuthService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.repository.UserAuthRepository;
import umc.puppymode.web.dto.KakaoFriendsResponseDTO;
import umc.puppymode.web.dto.KakaoTokenResponseDTO;
import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    private static final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    private static final String KAUTH_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final UserAuthRepository userAuthRepository;
    private final WebClient webClient = WebClient.create(KAUTH_USER_URL_HOST);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${auth.kakao.client_id}")
    private String kakaoClientId;

    /**
     * 카카오 회원의 정보를 가져옵니다.
     *
     * @param accessToken
     * @return 회원 정보 전체
     */
    public UserAuthInfoDTO getUserInfo(String accessToken) {
        KakaoUserInfoResponseDTO userInfo = requestKakaoAPI("/v2/user/me", accessToken, KakaoUserInfoResponseDTO.class);

//        log.info("[ Kakao Service ] Auth ID —> {} ", userInfo.getId());
//        log.info("[ Kakao Service ] NickName —> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
//        log.info("[ Kakao Service ] email —> {} ", userInfo.getKakaoAccount().getEmail());

        validateKakaoUserInfo(userInfo);

        return UserAuthInfoDTO.builder()
                .userAuthId(userInfo.getId().toString())
                .email(userInfo.getKakaoAccount().getEmail())
                .username(userInfo.getKakaoAccount().getProfile().getNickName())
                .authProvider(AuthProvider.KAKAO)
                .build();
    }

    /**
     * 카카오 친구 목록을 가져옵니다. (토큰 만료 시 자동 갱신)
     */
    public List<String> getFriendsList(String accessToken, Long userId) {
        try {
            return requestFriendsList(accessToken);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("401")) {  // 만료 처리
                log.warn("[Kakao Service] Access token expired or invalid. Refreshing...");
                try {
                    String newAccessToken = refreshAccessToken(userId); // access Token 재발급
                    return requestFriendsList(newAccessToken);
                } catch (RuntimeException refreshException) {
                    log.error("[Kakao Service] Failed to refresh access token. Re-login required.");
                    throw new RuntimeException("카카오 친구 목록을 가져올 수 없습니다. 재로그인이 필요합니다.", refreshException);
                }
            }
            throw e;
        }
    }

    /**
     * refreshToken을 사용하여 새로운 accessToken을 발급합니다.
     */
    public String refreshAccessToken(Long userId) {
        String refreshToken = userAuthRepository.findRefreshTokenByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        KakaoTokenResponseDTO tokenResponse = WebClient.create(KAUTH_TOKEN_URL)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "refresh_token")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("refresh_token", refreshToken)
                        .build())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("Failed to refresh Kakao access token");
        }

        if (tokenResponse.getRefreshToken() != null) {
            updateRefreshTokenInDB(userId, tokenResponse.getRefreshToken());
        }

        return tokenResponse.getAccessToken();
    }

    /**
     * user_auth 테이블의 refreshToken을 업데이트합니다.
     */
    private void updateRefreshTokenInDB(Long userId, String newRefreshToken) {
        userAuthRepository.updateRefreshToken(userId, newRefreshToken);
        log.info("[Kakao Service] Refresh token updated");
    }

    /**
     * requestKakaoAPI를 통해 친구 목록을 가져옵니다.
     */
    private List<String> requestFriendsList(String accessToken) {
        KakaoFriendsResponseDTO friendsResponse = requestKakaoAPI("/v1/api/talk/friends", accessToken, KakaoFriendsResponseDTO.class);

        if (friendsResponse == null || friendsResponse.getElements() == null) {
            throw new IllegalArgumentException("카카오 친구 목록을 가져올 수 없습니다.");
        }

        return friendsResponse.getElements().stream()
                .map(KakaoFriendsResponseDTO.Friend::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 공통 API 요청 메서드
     */
    private <T> T requestKakaoAPI(String path, String accessToken, Class<T> responseType) {
        T response = webClient.get()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(error -> {
                            log.error("[Kakao Service] 4XX Error: {}", error);
                            handleKakaoApiError(error);
                            return Mono.error(new RuntimeException("카카오 API 요청 실패: " + error));
                        }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("카카오 서버 오류")))
                .bodyToMono(responseType)
                .block();

//        logResponse(response);
        return response;
    }

    /**
     * 응답 데이터를 JSON으로 변환하여 로그를 출력합니다.
     */
    private void logResponse(Object response) {
        try {
            log.info("[ Kakao Service ] 응답: {}", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.error("[ Kakao Service ] JSON 변환 실패", e);
        }
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

    /**
     * 예외 처리
     */
    private void handleKakaoApiError(String errorResponse) {
        if (errorResponse.contains("\"code\": -401")) {
            throw new IllegalArgumentException("잘못된 Access Token입니다. 재로그인이 필요합니다.");
        }
        if (errorResponse.contains("\"code\": -402")) {
            throw new IllegalStateException("카카오 API 사용량 초과. 잠시 후 다시 시도하세요.");
        }
        if (errorResponse.contains("\"code\": -500")) {
            throw new RuntimeException("카카오 서버 내부 오류 발생. 나중에 다시 시도해주세요.");
        }
        throw new RuntimeException("카카오 API 요청 실패: " + errorResponse);
    }
}