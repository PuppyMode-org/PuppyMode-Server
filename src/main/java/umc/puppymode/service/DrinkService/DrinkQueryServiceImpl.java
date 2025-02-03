package umc.puppymode.service.DrinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public List<HangoverResponseDTO> getAllHangovers() { {
            return hangoverRepository.findAll().stream()
                    .map(item -> HangoverResponseDTO.builder()
                            .hangoverId(item.getHangoverId())
                            .hangoverName(item.getHangoverName())
                            .imageUrl(item.getImageUrl())
                            .build())
                    .collect(Collectors.toList());
        }
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

        // 해당 drinkItemId에 해당하는 술 항목 조회
        Optional<DrinkItem> drinkItemOpt = drinkItemRepository.findById(drinkItemId);

        // DrinkItem이 존재하고, 해당 drinkItemId가 사용자의 기록에 포함되어 있는지 확인
        if (drinkItemOpt.isPresent()) {
            DrinkItem drinkItem = drinkItemOpt.get();

            // 사용자가 해당 drinkItemId에 대해 작성한 기록이 있는지 확인
            List<DrinkHistoryItem> historyItems = drinkHistoryItemRepository.findByHistory_User_UserIdAndItem_ItemId(userId, drinkItemId);

            // 사용자가 해당 drinkItem에 대해 작성한 기록이 있을 경우에만 반환
            if (!historyItems.isEmpty()) {
                // 가장 최근 기록을 가져오기 (혹은 원하는 기록 기준으로 선택)
                DrinkHistoryItem historyItem = historyItems.get(0);

                return DrinkInfoResponseDTO.builder()
                        .drinkItemId(drinkItem.getItemId())
                        .drinkItemName(drinkItem.getItemName())
                        .imageUrl(drinkItem.getImageUrl())
                        .alcoholPercentage(drinkItem.getAlcoholPercentage())
                        .safetyValue(historyItem.getSafetyValue())  // historyItem에서 safetyValue 가져오기
                        .maxValue(historyItem.getMaxValue())    // historyItem에서 maxValue 가져오기
                        .build();
            }
        }

        // 사용자가 작성한 기록이 없으면 null 반환
        return null;
    }
}
