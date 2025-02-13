package umc.puppymode.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import umc.puppymode.domain.UserAuth;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.domain.enums.TokenType;
import umc.puppymode.repository.TokenRepository;
import umc.puppymode.repository.UserAuthRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.LoginResponseDTO;
import umc.puppymode.web.dto.UserAuthInfoDTO;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final UserAuthRepository userAuthRepository;

    @Transactional
    @Override
    public LoginResponseDTO createOrUpdateUser(UserAuthInfoDTO userInfo, AuthProvider authProvider, String refreshToken) {
        AtomicBoolean isNewUser = new AtomicBoolean(false);

        String authId = userInfo.getUserAuthId();
        String email = userInfo.getEmail();
        String newUsername = userInfo.getUsername();

        Optional<UserAuth> optionalUserAuth = userAuthRepository.findByUser_EmailAndAuthProvider(email, authProvider);

        UserAuth userAuth;
        User user;

        // 기존 회원일 경우
        if (optionalUserAuth.isPresent()) {
            userAuth = optionalUserAuth.get();
            user = userAuth.getUser();

            if (refreshToken != null && (userAuth.getRefreshToken() == null || !refreshToken.equals(userAuth.getRefreshToken()))) {
                log.info("Auth Refresh Token 갱신됨.");
                userAuth.setRefreshToken(refreshToken);
                userAuthRepository.save(userAuth);
            }

            if (newUsername != null && !newUsername.equals(user.getUsername())) {
                log.info("username 변경됨.");
                user.setUsername(newUsername);
                userRepository.save(user);
            }

            return generateLoginResponse(user, false);
        }

        // 신규 회원일 경우
        Optional<User> optionalUser = userRepository.findByEmail(email);

        user = optionalUser.map(existingUser -> {
            if (existingUser.getIsDeleted()) {
                // 탈퇴한 사용자 복구
                existingUser.setIsDeleted(false);
                return userRepository.save(existingUser);
            }
            return existingUser;
        }).orElseGet(() -> {
            // 새 사용자 생성
            isNewUser.set(true);
            User newUser = User.builder()
                    .email(email)
                    .username(userInfo.getUsername())
                    .points(0)
                    .receiveNotifications(false)
                    .isDeleted(false)
                    .build();
            return userRepository.save(newUser);
        });

        userAuth = UserAuth.builder()
                .user(user)
                .authProvider(authProvider)
                .authId(authId)
                .refreshToken(refreshToken)
                .build();
        userAuthRepository.save(userAuth);

        return generateLoginResponse(user, isNewUser.get());
    }

    /**
     * JWT 발급 및 응답 생성
     */
    private LoginResponseDTO generateLoginResponse(User user, boolean isNewUser) {
        // 인증 객체 생성
        Authentication authentication = new UserAuthentication(user.getUserId().toString(), null, null);
        String token = jwtTokenProvider.generateToken(authentication);

        LoginResponseDTO.LoginUserInfo loginUserInfo = LoginResponseDTO.LoginUserInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .isNewUser(isNewUser)
                .build();

        return LoginResponseDTO.builder()
                .accessToken(token)
                .refreshToken(null) //TODO: refresh 구현
                .userInfo(loginUserInfo)
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
