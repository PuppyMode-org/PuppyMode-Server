package umc.puppymode.service.DrinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.puppymode.domain.DrinkCategory;
import umc.puppymode.repository.DrinkCategoryRepository;
import umc.puppymode.repository.HangoverRepository;
import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrinkQueryServiceImpl implements DrinkQueryService {

    private final HangoverRepository hangoverRepository;
    private final DrinkCategoryRepository drinkCategoryRepository;

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
}
