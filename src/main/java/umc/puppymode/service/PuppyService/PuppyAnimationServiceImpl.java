package umc.puppymode.service.PuppyService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyAnimation;
import umc.puppymode.domain.PuppyAnimationImage;
import umc.puppymode.domain.enums.AnimationType;
import umc.puppymode.repository.PuppyAnimationRepository;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.web.dto.AnimationFramesResponseDTO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuppyAnimationServiceImpl implements PuppyAnimationService{

    private final PuppyAnimationRepository puppyAnimationRepository;
    private final PuppyRepository puppyRepository;

    @Override
    public AnimationFramesResponseDTO getAnimaitonFrames(AnimationType animationType, Long userId) {

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다."));

        // 해당하는 애니메이션 찾기
        PuppyAnimation puppyAnimation = puppyAnimationRepository.findByAnimationTypeAndPuppyTypeAndLevelName(
                        animationType,
                        puppy.getPuppyLevel().getPuppyType(),
                        puppy.getPuppyLevel().getLevelName()
                ).orElseThrow(() -> new IllegalArgumentException("해당하는 애니메이션을 찾을 수 없습니다."));

        // 애니메이션 프레임 가져오기
        List<PuppyAnimationImage> animationImages = puppyAnimation.getAnimationImages();

        // 이미지 URL 순서대로 정리
        List<String> imageUrls = animationImages.stream()
                .sorted(Comparator.comparingInt(PuppyAnimationImage::getFrameOrder))
                .map(PuppyAnimationImage::getImageUrl)
                .collect(Collectors.toList());

        return new AnimationFramesResponseDTO(animationType, imageUrls.size(), imageUrls);

    }

}
