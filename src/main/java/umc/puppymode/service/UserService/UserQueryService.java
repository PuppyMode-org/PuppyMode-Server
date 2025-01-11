package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.UserNotificationResponseDTO;

public interface UserQueryService {

    UserNotificationResponseDTO getUserNotificationStatus(Long userId);
}
