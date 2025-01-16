package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.DrinkService.DrinkQueryService;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class DrinkController {

    private final DrinkQueryService drinkQueryService;

    @GetMapping("hangover")
    @Operation(summary = "숙취 목록 조회 API", description = "숙취 목록을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<HangoverResponseDTO>>> getHangovers() {
        List<HangoverResponseDTO> responseDTO = drinkQueryService.getAllHangovers();
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("drinks/categories")
    @Operation(summary = "술 카테고리 조회 API", description = "술 카테고리를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getDrinkCategories() {
        List<CategoryResponseDTO> responseDTO = drinkQueryService.getAllDrinkCategories();
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("drinks/categories/{categoryId}")
    @Operation(summary = "카테고리 별 술 목록 조회 API", description = "카테고리 별 술 목록을 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<DrinkItemsByCategoryResponseDTO>> getDrinksByCategory(@PathVariable Long categoryId) {
        DrinkItemsByCategoryResponseDTO responseDTO = drinkQueryService.getAllDrinkItemsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
