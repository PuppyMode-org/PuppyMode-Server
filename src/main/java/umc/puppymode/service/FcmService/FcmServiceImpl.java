package umc.puppymode.service.FcmService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.web.dto.FCMDTO.FCMRequestDTO;
import umc.puppymode.web.dto.FCMDTO.FCMResponseDTO;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    @Value("${fcm.project-name}")
    private String projectName;

    @Value("${fcm.firebase-key-path}")
    private String firebaseConfigPath;

    private final ObjectMapper objectMapper;
    private GoogleCredentials googleCredentials;
    private final RestTemplate restTemplate;

    @PostConstruct
    public void initialize() {
        try {
            this.googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            // UTF-8 인코딩 설정
            restTemplate.getMessageConverters().add(0,
                    new StringHttpMessageConverter(StandardCharsets.UTF_8));

        } catch (IOException e) {
            log.error("FCM 초기화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("FCM 초기화 실패", e);
        }
    }

    /**
     * 푸시 메시지를 전송하는 비즈니스 로직
     *
     * @param fcmRequestDTO FCMRequestDTO 객체로 전달받은 푸시 알림 정보
     * @return ApiResponse 객체로 성공 또는 실패 결과 반환
     */
    @Override
    public ApiResponse<FCMResponseDTO> sendMessageTo(FCMRequestDTO fcmRequestDTO) {
        try {
            // FCM 메시지 만들기
            String message = makeMessage(fcmRequestDTO);

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getAccessToken());

            // 요청 본문 설정
            HttpEntity<String> entity = new HttpEntity<>(message, headers);
            String apiUrl = String.format("https://fcm.googleapis.com/v1/projects/%s/messages:send", projectName);

            log.debug("FCM 메시지 전송 시도. token: {}", fcmRequestDTO.getToken());

            // FCM API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 응답 상태 확인 후 ApiResponse 반환
            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("FCM 메시지 전송 성공. token: {}", fcmRequestDTO.getToken());

                FCMResponseDTO fcmResponseDTO = FCMResponseDTO.builder()
                        .validateOnly(true)
                        .message(new FCMResponseDTO.Message(
                                new FCMResponseDTO.Notification(
                                        fcmRequestDTO.getTitle(),
                                        fcmRequestDTO.getBody(),
                                        null
                                ),
                                fcmRequestDTO.getToken()
                        ))
                        .build();
                return ApiResponse.onSuccess(fcmResponseDTO);
            } else {
                log.error("FCM 메시지 전송 실패. Status: {}, token: {}",
                        response.getStatusCode(),
                        fcmRequestDTO.getToken()
                );
                throw new GeneralException(ErrorStatus.FIREBASE_MESSAGE_SEND_FAILED);
            }
        } catch (IOException e) {
            log.error("Firebase 에러 발생. token: {}, error: {}",
                    fcmRequestDTO.getToken(),
                    e.getMessage(),
                    e
            );
            throw new GeneralException(ErrorStatus.FIREBASE_ERROR);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 중 예상치 못한 에러 발생. token: {}, error: {}",
                    fcmRequestDTO.getToken(),
                    e.getMessage(),
                    e
            );
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Firebase 인증 토큰을 발급받는 메서드
     *
     * @return Bearer 토큰
     */
    private String getAccessToken() throws IOException {
        try {
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            log.error("Access Token 갱신 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * FCM 전송 메시지를 구성하는 메서드
     *
     * @param fcmRequestDTO FCMRequestDTO 객체
     * @return JSON 형식의 문자열
     */
    private String makeMessage(FCMRequestDTO fcmRequestDTO) throws JsonProcessingException {
        FCMResponseDTO fcmMessageDto = FCMResponseDTO.builder()
                .message(FCMResponseDTO.Message.builder()
                        .token(fcmRequestDTO.getToken())
                        .notification(FCMResponseDTO.Notification.builder()
                                .title(fcmRequestDTO.getTitle())
                                .body(fcmRequestDTO.getBody())
                                .image(null)
                                .build())
                        .build())
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessageDto);
    }
}