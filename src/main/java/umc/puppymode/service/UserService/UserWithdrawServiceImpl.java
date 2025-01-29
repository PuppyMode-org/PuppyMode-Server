package umc.puppymode.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.domain.User;
import umc.puppymode.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWithdrawServiceImpl implements UserWithdrawService {

    private final UserRepository userRepository;

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없거나 이미 탈퇴한 상태입니다."));

        user.deactivate();
    }

}
