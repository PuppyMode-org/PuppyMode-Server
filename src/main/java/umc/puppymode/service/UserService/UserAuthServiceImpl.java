package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.config.security.JwtTokenProvider;
import umc.puppymode.config.security.UserAuthentication;
import umc.puppymode.domain.Token;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.TokenType;
import umc.puppymode.repository.TokenRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserInfoDTO;


@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    public LoginResponseDTO createOrUpdateUser(KakaoUserInfoResponseDTO userInfo) {
        User user = userRepository.findByEmail(userInfo.getKakaoAccount().getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(userInfo.getKakaoAccount().getEmail())
                            .username(userInfo.getKakaoAccount().getProfile().getNickName())
                            .points(0)
                            .receiveNotifications(false)
                            .build();
                    return userRepository.save(newUser);
                });

        // 현재 사용자 인증 객체 생성
        Authentication authentication = new UserAuthentication(user.getUserId().toString(), null, null);

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(authentication);

        // UserInfoDTO 생성
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        // LoginResponseDTO 생성 및 반환
        return LoginResponseDTO.builder()
                .jwt(token)
                .userInfo(userInfoDTO)
                .build();

    }

    @Override
    public Long getCurrentUserId() {

        // 인증 객체를 SecurityContext에서 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 인증 객체가 UserAuthentication 타입이 아닌 경우 예외 발생
        if (authentication == null || !(authentication instanceof UserAuthentication)) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        // 인증된 사용자 정보가 있으면, 그 사용자 ID를 반환
        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
    }

    @Transactional
    @Override
    public void saveFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // FCM 토큰을 찾고, 없으면 새로 생성
        Token token = tokenRepository.findByUserAndTokenType(user, TokenType.FCM)
                .orElse(null);

        if (token == null) {
            // 기존에 FCM 토큰이 없으면 새로 생성하고 저장
            token = Token.builder()
                    .user(user)
                    .tokenType(TokenType.FCM)
                    .token(fcmToken)
                    .build();
            tokenRepository.save(token);
        } else if (!fcmToken.equals(token.getToken())) {
            // 기존 토큰과 다른 경우에만 업데이트
            token.setToken(fcmToken);
            tokenRepository.save(token);
        }
    }

    @Override
    public LoginResponseDTO loginWithFcmToken(LoginResponseDTO loginResponseDTO, String fcmToken) {
        if (StringUtils.hasText(fcmToken)) {
            saveFcmToken(loginResponseDTO.getUserInfo().getUserId(), fcmToken);
        }
        return loginResponseDTO;
    }

}
