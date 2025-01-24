package umc.puppymode.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.service.PuppyService.PuppyItemService;
import umc.puppymode.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import umc.puppymode.service.UserService.UserAuthService;

import java.util.Map;

@RestController
@RequestMapping("/puppies")
@RequiredArgsConstructor
public class PuppyItemController {

    private final PuppyItemService puppyItemService;
    private final UserAuthService userAuthService;

    @Operation(summary = "카테고리 목록 조회 API", description = "꾸미기 아이템의 카테고리 목록을 조회하는 API")
    @GetMapping("/categories")
    public ApiResponse<Map<String, Object>> getAllCategories() {
        Map<String, Object> categories = puppyItemService.getAllCategories();
        return new ApiResponse<>(
                true,
                "SUCCESS_GET_PUPPY_CATEGORIES",
                "카테고리 목록 조회 성공",
                categories // Map 형태로 반환
        );
    }

    @Operation(summary = "카테고리 별 아이템 목록 조회 API", description = "요청받은 카테고리에 해당하는 아이템 목록을 조회하는 API")
    @GetMapping("/{categoryId}/items")
    public ApiResponse<Map<String, Object>> getItemsByCategory(@PathVariable Long categoryId) {
        Long userId = userAuthService.getCurrentUserId();

        return new ApiResponse<>(
                true,
                "SUCCESS_GET_PUPPY_CATEGORY_ITEMS",
                "카테고리 별 아이템 목록 조회 성공",
                puppyItemService.getItemsByCategory(categoryId, userId) // Map 형태로 반환
        );
    }

    @Operation(summary = "아이템 구매 API", description = "아이템을 구매하고 잔여 포인트와 아이템 ID를 반환하는 API")
    @PostMapping("/{categoryId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> purchaseItem(
            @PathVariable Long categoryId,
            @PathVariable Long itemId) {

        Long userId = userAuthService.getCurrentUserId();

        Map<String, Object> result = puppyItemService.purchaseItem(categoryId, itemId, userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "SUCCESS_POST_PUPPY_ITEM", "아이템 구매 성공", result)
        );
    }

    @Operation(summary = "아이템 착용 API", description = "강아지에게 아이템을 착용시키는 API")
    @PostMapping("/{categoryId}/items/{itemId}/equip")
    public ApiResponse<Map<String, Object>> equipItem(@PathVariable Long categoryId,
                                                      @PathVariable Long itemId) {
        Long userId = userAuthService.getCurrentUserId();

        Map<String, Object> result = puppyItemService.equipItem(categoryId, itemId, userId);
        return new ApiResponse<>(true, "SUCCESS_EQUIP_PUPPY_ITEM", "아이템 착용 성공", result);
    }


}
