package umc.puppymode.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.converter.UserNotificationConverter;
import umc.puppymode.domain.mapping.UserNotification;
import umc.puppymode.repository.UserNotificationRepository;
import umc.puppymode.web.dto.UserNotificationResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserNotificationQueryServiceImpl implements UserNotificationQueryService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserNotificationConverter userNotificationConverter;

    @Override
    public List<UserNotificationResponseDTO> getUserNotificationStatus(Long userId) {
        List<UserNotification> userNotifications = userNotificationRepository.findAllByUser_UserId(userId);
        return userNotificationConverter.toResponseDtoList(userNotifications);
    }
}
