package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.User;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.UserRequestDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    @Override
    public void updateUserNotificationStatus(Long userId, UserRequestDTO userRequestDTO) {

        // 사용자 정보 조회, 존재하지 않으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 알림 수신 여부가 기존 값과 동일하면 예외 발생
        if (user.getReceiveNotifications() == userRequestDTO.isReceiveNotifications()) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST_SAME_STATE);
        }

        // 알림 수신 여부 업데이트
        user.setReceiveNotifications(userRequestDTO.isReceiveNotifications());
    }
}
