package umc.puppymode.service.NotificationService;

import umc.puppymode.web.dto.UserNotificationResponseDTO;

import java.util.List;

public interface UserNotificationQueryService {

    List<UserNotificationResponseDTO> getUserNotificationStatus(Long userId);
}
