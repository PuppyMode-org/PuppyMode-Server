package umc.puppymode.service.UserService;

import umc.puppymode.domain.Puppy;
import umc.puppymode.web.dto.UserResponseDTO;


public interface UserQueryService {

    // 사용자 알림 수신 상태 조회
    UserResponseDTO getUserNotificationStatus(Long userId);

    // 사용자 id로 강아지 정보 조회
    Puppy getUserPuppy(Long userId);
}
