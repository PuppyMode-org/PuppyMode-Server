package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.domain.enums.AnimationType;
import umc.puppymode.service.PuppyService.PuppyAnimationService;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.web.dto.AnimationFramesResponseDTO;


@RestController
@RequestMapping("/puppies/animations")
@RequiredArgsConstructor
public class PuppyAnimationController {
    private final PuppyAnimationService puppyAnimationService;
    private final UserAuthService userAuthService;

    @Operation(summary = "애니메이션 프레임 조회 API", description = "강아지 애니메이션의 프레임 이미지 목록을 조회하는 API")
    @GetMapping("/frames")
    public ApiResponse<AnimationFramesResponseDTO> getAnimationFrames(
            @RequestParam AnimationType animationType) {
        Long userId = userAuthService.getCurrentUserId();
        AnimationFramesResponseDTO animationFramesResponseDTO = puppyAnimationService.getAnimaitonFrames(animationType, userId);
        return ApiResponse.onSuccess(animationFramesResponseDTO);
    }
}
