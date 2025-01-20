package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.UserResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final PuppyRepository puppyRepository;
    private final Logger logger = LoggerFactory.getLogger(UserQueryServiceImpl.class);

    @Override
    public UserResponseDTO getUserNotificationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return new UserResponseDTO(user.getReceiveNotifications());
    }

    @Override
    public List<String> getAllFcmTokens() {
        return userRepository.findAllFcmTokensWithNotification();
    }

    @Override
    public User getUserByFcmToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new GeneralException(ErrorStatus.FIREBASE_MISSING_TOKEN);
        }
        return userRepository.findByFcmToken(token)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public Puppy getUserPuppy(Long userId) {
        // userId로 강아지 찾기
        return puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PUPPY_NOT_FOUND));
    }
}
