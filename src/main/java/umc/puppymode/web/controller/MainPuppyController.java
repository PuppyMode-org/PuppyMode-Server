package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.MainPuppyService.MainPuppyCommandService;
import umc.puppymode.service.MainPuppyService.MainPuppyQueryService;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/puppies")
public class MainPuppyController {

    private final MainPuppyCommandService mainPuppyCommandService;
    private final MainPuppyQueryService mainPuppyQueryService;
    private final UserAuthService userAuthService;

    @PostMapping
    @Operation(summary = "강아지 랜덤 선택 API", description = "강아지를 랜덤으로 선택하는 API입니다.")
    public ApiResponse<MainPuppyResDTO.RandomPuppyViewDTO> getRandomPuppy() {

        Long userId = userAuthService.getCurrentUserId();
        MainPuppyResDTO.RandomPuppyViewDTO randomPuppyViewDTO = mainPuppyCommandService.getRandomPuppy(userId);
        return ApiResponse.onSuccess(randomPuppyViewDTO);
    }

    @GetMapping
    @Operation(summary = "사용자 강아지 정보 조회 API", description = "사용자의 강아지 정보를 조회하는 API입니다.")
    public ApiResponse<MainPuppyResDTO.UserPuppyViewDTO> getUserPuppy() {

        Long userId = userAuthService.getCurrentUserId();
        MainPuppyResDTO.UserPuppyViewDTO userPuppyViewDTO = mainPuppyQueryService.getUserPuppy(userId);
        return ApiResponse.onSuccess(userPuppyViewDTO);
    }

    @PatchMapping
    @Operation(summary = "강아지 이름 수정 API", description = "강아지의 이름을 수정하는 API입니다.")
    public ApiResponse<String> updatePuppyName(
            @RequestParam Long puppyId,
            @RequestParam String newPuppyName) {

        Long userId = userAuthService.getCurrentUserId();
        String updatedPuppyName = mainPuppyCommandService.updatePuppyName(userId, puppyId, newPuppyName);
        return ApiResponse.onSuccess(updatedPuppyName);
    }

    @PostMapping("/play")
    @Operation(summary = "강아지 놀아주기 API", description = "강아지 놀아주기를 실행하는 API입니다.")
    public ApiResponse<MainPuppyResDTO.PlayResDTO> playWithPuppy(
            @RequestParam Long puppyId) {

        Long userId = userAuthService.getCurrentUserId();
        MainPuppyResDTO.PlayResDTO playResDTO = mainPuppyCommandService.platWithPuppy(userId, puppyId);
        return ApiResponse.onSuccess(playResDTO);
    }

    @DeleteMapping
    @Operation(summary = "강아지 객체 삭제 API", description = "현재 유저의 강아지를 hard delete하는 API입니다. (실사용 목적 x)")
    public ApiResponse<String> deletePuppy() {

        Long userId = userAuthService.getCurrentUserId();
        String result = mainPuppyCommandService.deletePuppy(userId);
        return ApiResponse.onSuccess(result);
    }
}
