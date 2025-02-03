package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.DrinkService.DrinkCommandService;
import umc.puppymode.service.DrinkService.DrinkQueryService;
import umc.puppymode.service.AuthService.UserAuthService;
import umc.puppymode.web.dto.DrinkRequestDTO;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drinks")
public class DrinkController {

    private final DrinkCommandService drinkCommandService;
    private final DrinkQueryService drinkQueryService;
    private final UserAuthService userAuthService;

    @GetMapping("hangover")
    @Operation(summary = "숙취 목록 조회 API", description = "숙취 목록을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<HangoverResponseDTO>>> getHangovers() {
        List<HangoverResponseDTO> responseDTO = drinkQueryService.getAllHangovers();
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("categories")
    @Operation(summary = "술 카테고리 조회 API", description = "술 카테고리를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getDrinkCategories() {
        List<CategoryResponseDTO> responseDTO = drinkQueryService.getAllDrinkCategories();
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("categories/{categoryId}")
    @Operation(summary = "카테고리 별 술 목록 조회 API", description = "카테고리 별 술 목록을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<DrinkItemsByCategoryResponseDTO>> getDrinksByCategory(@PathVariable Long categoryId) {
        DrinkItemsByCategoryResponseDTO responseDTO = drinkQueryService.getAllDrinkItemsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @PostMapping("record")
    @Operation(summary = "음주 기록 생성 API", description = "음주 기록을 생성하는 API입니다.")
    public ResponseEntity<ApiResponse<DrinksRecordResponseDTO>> postDrinkRecord(@RequestBody DrinkRequestDTO.DrinkRecordDTO drinkRecordDTO) {
        Long userId = userAuthService.getCurrentUserId();
        DrinksRecordResponseDTO responseDTO = drinkCommandService.postDrinksRecord(userId, drinkRecordDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @PostMapping("feed")
    @Operation(summary = "먹이 주기 API", description = "획득한 먹이를 주는 API입니다.")
    public ResponseEntity<ApiResponse<FeedResponseDTO>> postFeed() {
        Long userId = userAuthService.getCurrentUserId();
        FeedResponseDTO  responseDTO = drinkCommandService.postFeed(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("capacity")
    public ResponseEntity<ApiResponse<DrinkInfoResponseDTO>> getDrinkInfo(@RequestParam Long drinkItemId) {
        Long userId = userAuthService.getCurrentUserId();
        DrinkInfoResponseDTO responseDTO = drinkQueryService.getDrinkInfo(userId, drinkItemId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
