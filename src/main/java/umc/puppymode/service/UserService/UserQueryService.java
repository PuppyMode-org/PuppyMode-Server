package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.UserResponseDTO;

public interface UserQueryService {

    UserResponseDTO getUserNotificationStatus(Long userId);
}
