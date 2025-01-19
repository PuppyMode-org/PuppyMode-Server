package umc.puppymode.service.UserService;

import umc.puppymode.web.dto.UserResponseDTO;

import java.util.List;

public interface UserQueryService {

    UserResponseDTO getUserNotificationStatus(Long userId);
    List<String> getAllFcmTokens();
}
