package umc.puppymode.service.UserService;

import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.web.dto.UserResponseDTO;

import java.util.List;

public interface UserQueryService {

    // 사용자 알림 수신 상태 조회
    UserResponseDTO getUserNotificationStatus(Long userId);

    // 모든 FCM 토큰 조회
    List<String> getAllFcmTokens();

    // FCM 토큰으로 사용자 조회
    User getUserByFcmToken(String token);

    // 사용자 id로 강아지 정보 조회
    Puppy getUserPuppy(Long userId);
}
