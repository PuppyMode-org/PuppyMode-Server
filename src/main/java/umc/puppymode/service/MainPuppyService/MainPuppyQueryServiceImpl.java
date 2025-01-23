package umc.puppymode.service.MainPuppyService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.handler.TempHandler;
import umc.puppymode.converter.MainPuppyConverter;
import umc.puppymode.domain.Puppy;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MainPuppyQueryServiceImpl implements MainPuppyQueryService {

    private final PuppyRepository puppyRepository;
    private final UserRepository userRepository;

    @Override
    // 사용자의 강아지 정보를 조회
    public MainPuppyResDTO.UserPuppyViewDTO getUserPuppy(Long userId) {

        userRepository.findById(userId).orElseThrow(() -> new TempHandler(ErrorStatus.USER_NOT_FOUND));
        Puppy puppy = puppyRepository.findByUserId(userId).orElseThrow(() -> new TempHandler(ErrorStatus.PUPPY_NOT_FOUND));

        return MainPuppyConverter.toUserPuppyViewDTO(puppy);
    }
}
