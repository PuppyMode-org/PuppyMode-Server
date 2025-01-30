package umc.puppymode.service.PuppyService;

import umc.puppymode.domain.enums.AnimationType;
import umc.puppymode.web.dto.AnimationFramesResponseDTO;

public interface PuppyAnimationService {
    AnimationFramesResponseDTO getAnimaitonFrames(AnimationType animationType, Long userId);
}
