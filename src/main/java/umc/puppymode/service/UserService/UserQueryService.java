package umc.puppymode.service.UserService;

import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.web.dto.UserResponseDTO;

import java.util.List;

public interface UserQueryService {

    UserResponseDTO getUserNotificationStatus(Long userId);
    List<String> getAllFcmTokens();
    User getUserByFcmToken(String token);
    Puppy getUserPuppy(Long userId);
}
