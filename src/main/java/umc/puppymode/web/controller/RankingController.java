package umc.puppymode.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.RankingService.RankingQueryService;
import umc.puppymode.service.UserService.UserAuthService;
import umc.puppymode.web.dto.RankingResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rankings")
public class RankingController {

    private final RankingQueryService rankingQueryService;
    private final UserAuthService userAuthService;

    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<RankingResponseDTO>> getFriendRankings(
            @RequestParam List<String> authIds,
            @RequestParam int page,
            @RequestParam int size) {

        Long userId = userAuthService.getCurrentUserId();

        RankingResponseDTO responseDTO = rankingQueryService.getFriendRankings(authIds, page, size, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<RankingResponseDTO>> getGlobalRankings(
            @RequestParam int page,
            @RequestParam int size) {

        Long userId = userAuthService.getCurrentUserId();

        RankingResponseDTO responseDTO = rankingQueryService.getGlobalRankings(page, size, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
