package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.MainPuppyService.MainPuppyCommandService;
import umc.puppymode.service.MainPuppyService.MainPuppyQueryService;
import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/puppies")
public class MainPuppyController {

    private final MainPuppyCommandService mainPuppyCommandService;
    private final MainPuppyQueryService mainPuppyQueryService;

    @PostMapping
    @Operation(summary = "강아지 랜덤 선택 API", description = "강아지를 랜덤으로 선택하는 API입니다.")
    public ApiResponse<MainPuppyResDTO.RandomPuppyViewDTO> getRandomPuppy() {

        MainPuppyResDTO.RandomPuppyViewDTO randomPuppyViewDTO = mainPuppyCommandService.getRandomPuppy();
        return ApiResponse.onSuccess(randomPuppyViewDTO);
    }

    // 추후에 유저 시큐리티 부분 구현되면, @CurrentUser와 같은 AuthUser를 사용하여 강아지 객체를 찾도록 수정 예정
    @GetMapping
    @Operation(summary = "사용자 강아지 정보 조회 API", description = "사용자의 강아지 정보를 조회하는 API입니다.")
    public ApiResponse<MainPuppyResDTO.UserPuppyViewDTO> getUserPuppy(
            @RequestParam Long userId) {

        MainPuppyResDTO.UserPuppyViewDTO userPuppyViewDTO = mainPuppyQueryService.getUserPuppy(userId);
        return ApiResponse.onSuccess(userPuppyViewDTO);
    }

    // 이 api 역시 추후에 puppyId가 아닌 @CurrentUser 등을 사용하여 수정할 강아지 객체를 찾도록 수정 예정
    @PatchMapping
    @Operation(summary = "강아지 이름 수정 API", description = "강아지의 이름을 수정하는 API입니다.")
    public ApiResponse<String> updatePuppyName(
            @RequestParam Long puppyId,
            @RequestParam String newPuppyName) {

        String updatedPuppyName = mainPuppyCommandService.updatePuppyName(puppyId, newPuppyName);
        return ApiResponse.onSuccess(updatedPuppyName);
    }

    // 이 api도 @CurrentUser 등을 사용하여 유저 정보를 얻도록 수정 예정
    @PostMapping("/play")
    @Operation(summary = "강아지 놀아주기 API", description = "강아지 놀아주기를 실행하는 API입니다.")
    public ApiResponse<MainPuppyResDTO.PlayResDTO> playWithPuppy(
            @RequestParam Long userId,
            @RequestParam Long puppyId) {

        MainPuppyResDTO.PlayResDTO playResDTO = mainPuppyCommandService.platWithPuppy(userId, puppyId);

        // 낙관적 업데이트 사용 시, 현재 리턴값인 playResDTO 내부의 값을 사용하여 유효성 판단
        return ApiResponse.onSuccess(playResDTO);
    }
}
