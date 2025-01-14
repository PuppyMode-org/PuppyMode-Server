package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.User;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.UserResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getUserNotificationStatus(Long userId) {

        // 사용자 정보 조회, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 사용자의 알림 수신 여부 반환
        return new UserResponseDTO(user.getReceiveNotifications());
    }

    @Override
    public List<String> getAllFcmTokens() {

        return userRepository.findAllFcmTokensWithNotification();
    }
}
