package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.User;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.UserInfoResponseDTO;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;

    public UserInfoResponseDTO getUserInfo(Long userId) {
        // db에서 User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // DTO 생성
        return new UserInfoResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
