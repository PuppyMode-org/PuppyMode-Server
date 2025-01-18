package umc.puppymode.service.MainPuppyService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.handler.TempHandler;
import umc.puppymode.converter.MainPuppyConverter;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.PuppyType;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MainPuppyCommandServiceImpl implements MainPuppyCommandService {

    private final PuppyRepository puppyRepository;
    private final UserRepository userRepository;

    @Override
    // 랜덤으로 강아지를 선택
    public MainPuppyResDTO.RandomPuppyViewDTO getRandomPuppy() {

        Random RANDOM = new Random();
        PuppyType[] types = PuppyType.values();
        PuppyType type = types[RANDOM.nextInt(types.length)];

        // 강아지 종의 이름과, 해당 종의 레벨 1 이미지를 ResponseDTO로 만들어 반환
        return MainPuppyConverter.toRandomPuppyViewDTO(type.getType(), type.getImageUrl());
    }

    @Override
    // 강아지의 이름을 수정
    public String updatePuppyName(Long puppyId, String newPuppyName) {

        Puppy puppy = puppyRepository.findById(puppyId).orElseThrow(() -> new TempHandler(ErrorStatus.PUPPY_NOT_FOUND));
        puppy.updatePuppyName(newPuppyName);

        return puppy.getPuppyName();
    }

    @Override
    // 강아지 놀아주기 실행
    public MainPuppyResDTO.PlayResDTO platWithPuppy(Long userId, Long puppyId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new TempHandler(ErrorStatus.USER_NOT_FOUND));
        Puppy puppy = puppyRepository.findById(puppyId).orElseThrow(() -> new TempHandler(ErrorStatus.PUPPY_NOT_FOUND));
        // 강아지가 현재 사용자의 강아지가 아닌 경우 에러 발생
        if (!userId.equals(puppy.getUser().getUserId())) {
            throw new TempHandler(ErrorStatus.UNAUTHORIZED_PUPPY_ACCESS);
        }
        // 사용자 포인트 10p 증가
        user.updatePoints(10);
        // 현재 레벨의 전체 경험치의 5% 만큼의 경험치 계산
        Integer fivePercentExp = (puppy.getPuppyLevel().getLevelMaxEXP() - puppy.getPuppyLevel().getLevelMinExp()) * 5 / 100;
        puppy.updatePuppyExp(fivePercentExp); // 이 내부에서 레벨 업도 진행해준다고 가정

        return MainPuppyConverter.toPlayResDTO(puppy);
    }
}
