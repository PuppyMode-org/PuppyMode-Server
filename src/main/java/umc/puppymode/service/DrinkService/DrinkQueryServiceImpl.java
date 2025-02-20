package umc.puppymode.service.DrinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.DrinkCategory;
import umc.puppymode.domain.DrinkHistoryItem;
import umc.puppymode.domain.DrinkItem;
import umc.puppymode.repository.DrinkCategoryRepository;
import umc.puppymode.repository.DrinkHistoryItemRepository;
import umc.puppymode.repository.DrinkItemRepository;
import umc.puppymode.repository.HangoverRepository;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DrinkQueryServiceImpl implements DrinkQueryService {

    private final HangoverRepository hangoverRepository;
    private final DrinkCategoryRepository drinkCategoryRepository;
    private final DrinkItemRepository drinkItemRepository;
    private final DrinkHistoryItemRepository drinkHistoryItemRepository;

    public List<HangoverResponseDTO> getAllHangovers() {
        return hangoverRepository.findAll().stream()
                .map(item -> HangoverResponseDTO.builder()
                        .hangoverId(item.getHangoverId())
                        .hangoverName(item.getHangoverName())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getAllDrinkCategories() {
        return drinkCategoryRepository.findAll().stream()
                .map(item -> CategoryResponseDTO.builder()
                        .categoryId(item.getCategoryId())
                        .categoryName(item.getCategoryName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public DrinkItemsByCategoryResponseDTO getAllDrinkItemsByCategory(Long categoryId) {
        // 카테고리 조회
        DrinkCategory category = drinkCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));

        // DrinkItem 변환
        List<DrinksResponseDTO> items = category.getItems().stream()
                .map(item -> DrinksResponseDTO.builder()
                        .itemId(item.getItemId())
                        .itemName(item.getItemName())
                        .alcoholPercentage(item.getAlcoholPercentage())
                        .volumeMl(item.getVolumeMl())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        // 카테고리 및 아이템 데이터를 포함한 응답 DTO 생성
        return DrinkItemsByCategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .items(items)
                .build();
    }

    @Override
    public DrinkInfoResponseDTO getDrinkInfo(Long userId, Long drinkItemId) {
        DrinkItem drinkItem = drinkItemRepository.findById(drinkItemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DRINK_ITEM_NOT_FOUND));

        List<DrinkHistoryItem> historyItems = drinkHistoryItemRepository.findByHistory_User_UserIdAndItem_ItemId(userId, drinkItemId);

        if (!historyItems.isEmpty()) {
            DrinkHistoryItem historyItem = historyItems.get(0);
            // 평균 주량 계산
            float average = (float) historyItems.stream()
                    .mapToDouble(item -> convertToAlcoholAmount(item.getItem().getItemId(), item.getUnit(), item.getValue()))
                    .average()
                    .orElse(0.0);

            float safetyValueBottle = 0;
            float safetyValueGlass = 0;
            float maxValueBottle = 0;
            float maxValueGlass = 0;

            // 소주나 맥주에 따라 병과 잔의 값을 다르게 설정
            if (drinkItem.getCategory().getCategoryId() == 1) { // 소주
                safetyValueBottle = historyItem.getSafetyValue() / (360 * historyItem.getItem().getAlcoholPercentage() / 100);
                safetyValueGlass = historyItem.getSafetyValue() / (50 * historyItem.getItem().getAlcoholPercentage() / 100);

                maxValueBottle = historyItem.getMaxValue() / (360 * historyItem.getItem().getAlcoholPercentage() / 100);
                maxValueGlass = historyItem.getMaxValue() / (50 * historyItem.getItem().getAlcoholPercentage() / 100);
            } else if (drinkItem.getCategory().getCategoryId() == 2) { // 맥주
                safetyValueBottle = historyItem.getSafetyValue() / (500 * historyItem.getItem().getAlcoholPercentage() / 100);
                safetyValueGlass = historyItem.getSafetyValue() / (300 * historyItem.getItem().getAlcoholPercentage() / 100);

                maxValueBottle = historyItem.getMaxValue() / (500 * historyItem.getItem().getAlcoholPercentage() / 100);
                maxValueGlass = historyItem.getMaxValue() / (300 * historyItem.getItem().getAlcoholPercentage() / 100);
            }
            return DrinkInfoResponseDTO.builder()
                    .drinkItemId(drinkItem.getItemId())
                    .drinkItemName(drinkItem.getItemName())
                    .imageUrl(drinkItem.getImageUrl())
                    .alcoholPercentage(drinkItem.getAlcoholPercentage())
                    .safetyValue(historyItem.getSafetyValue())
                    .maxValue(historyItem.getMaxValue())
                    .safetyValueBottle(safetyValueBottle)  // 안전 주량 병
                    .safetyValueGlass(safetyValueGlass)    // 안전 주량 잔
                    .maxValueBottle(maxValueBottle)        // 치사량 병
                    .maxValueGlass(maxValueGlass)          // 치사량 잔
                    .average(average)                      // 평균 주량
                    .build();
        }
        return DrinkInfoResponseDTO.builder()
                .drinkItemId(drinkItem.getItemId())
                .drinkItemName(drinkItem.getItemName())
                .imageUrl(drinkItem.getImageUrl())
                .alcoholPercentage(drinkItem.getAlcoholPercentage())
                .safetyValue(0f)
                .maxValue(0f)
                .safetyValueBottle(0f)
                .safetyValueGlass(0f)
                .maxValueBottle(0f)
                .maxValueGlass(0f)
                .average(0f)
                .build();

    }

    private float convertToAlcoholAmount(Long drinkItemId, String unit, float value) {
        DrinkItem drinkItem = drinkItemRepository.findById(drinkItemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DRINK_ITEM_NOT_FOUND));
        float volume = convertToMl(drinkItem.getCategory().getCategoryId(), unit, value);
        float alcoholPercentage = drinkItem.getAlcoholPercentage();
        return (volume * alcoholPercentage) / 100;
    }

    private float convertToMl(Long drinkCategoryId, String unit, float value) {
        // 소주
        if (drinkCategoryId == 1) {
            switch (unit) {
                case "잔":
                    return value * 50;
                case "병":
                    return value * 360;
            }
        }
        // 맥주
        else if (drinkCategoryId == 2) {
            switch (unit) {
                case "잔":
                    return value * 300;
                case "병":
                    return value * 500;
            }
        }
        return value;
    }
}
