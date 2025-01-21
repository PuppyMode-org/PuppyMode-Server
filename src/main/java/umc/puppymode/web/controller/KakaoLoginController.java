package umc.puppymode.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.service.KakaoService;
import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoLoginController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            // 카카오에서 Access Token 가져오기
            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            log.info("Received authorization code: {}", code);

            // 사용자 정보 가져오기
            KakaoUserInfoResponseDTO userInfo = kakaoService.getUserInfo(accessToken);

            //TODO: null 검사 후 response 파싱하여 로그인/회원가입 로직 추가. 자체 jwt 토큰으로 인가해야함.
            // 카카오 리턴 id 를 식별자로 사용.
            // db에 id 존재하면 로그인, 없으면 회원가입 진행.
            // password: null, auth id 만 추가하여 회원가입.

            // 성공 응답 반환
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("카카오 로그인 오류 발생: {}", e.getMessage(), e);

            // 실패 응답 반환
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
//                    "message", "카카오 로그인 오류가 발생했습니다.",
//                    "error", e.getMessage()
//            ));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}