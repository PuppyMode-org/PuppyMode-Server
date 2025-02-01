package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.RankingService.RankingQueryService;
import umc.puppymode.service.AuthService.UserAuthService;
import umc.puppymode.web.dto.RankingResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rankings")
public class RankingController {

    private final RankingQueryService rankingQueryService;
    private final UserAuthService userAuthService;

    @GetMapping("/friends")
    @Operation(summary = "카카오 친구 랭킹 조회 API", description = "친구인 사용자 랭킹을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<RankingResponseDTO>> getFriendRankings(
            @Parameter(description = "Kakao 친구 Id 목록 (개별 입력)", example = "authIds=123456&authIds=654321")
            @RequestParam List<String> authIds,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam int page,
            @Parameter(description = "한 페이지당 데이터 수", example = "10")
            @RequestParam int size) {

        Long userId = userAuthService.getCurrentUserId();

        RankingResponseDTO responseDTO = rankingQueryService.getFriendRankings(authIds, page, size, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("/global")
    @Operation(summary = "전체 랭킹 조회 API", description = "전체 사용자 랭킹을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<RankingResponseDTO>> getGlobalRankings(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam int page,
            @Parameter(description = "한 페이지당 데이터 수", example = "10") @RequestParam int size) {

        Long userId = userAuthService.getCurrentUserId();

        RankingResponseDTO responseDTO = rankingQueryService.getGlobalRankings(page, size, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
