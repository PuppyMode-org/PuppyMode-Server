package umc.puppymode.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.UserInfoResponseDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    private final UserRepository userRepository;
    private final PuppyRepository puppyRepository;

    @Override
    public UserInfoResponseDTO getUserInfo(Long userId) {
        // 사용자 정보 조회
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 강아지 정보 조회
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Puppy not found. 강아지를 선택해 주세요."));

        UserInfoResponseDTO.UserPuppyInfo userPuppyInfo = UserInfoResponseDTO.UserPuppyInfo.builder()
                .puppyId(puppy.getPuppyId())
                .puppyName(puppy.getPuppyName())
                .build();

        return UserInfoResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .puppy(userPuppyInfo)
                .build();
    }
}
