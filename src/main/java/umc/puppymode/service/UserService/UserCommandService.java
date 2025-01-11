package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.UserRequestDTO;

public interface UserCommandService {

    void updateUserNotificationStatus(Long userId, UserRequestDTO userRequestDTO);
}
