package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.config.security.JwtTokenProvider;
import umc.puppymode.config.security.UserAuthentication;
import umc.puppymode.domain.User;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.KakaoUserInfoResponseDTO;
import umc.puppymode.web.dto.LoginResponseDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

        // 응답 DTO 생성 및 반환
        return new LoginResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), token, user.getPoints());
    }
}
