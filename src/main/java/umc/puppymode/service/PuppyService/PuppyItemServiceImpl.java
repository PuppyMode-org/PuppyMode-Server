package umc.puppymode.service.PuppyService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.PuppyItemCategory;
import umc.puppymode.domain.mapping.PuppyCustomization;
import umc.puppymode.repository.*;
import umc.puppymode.web.dto.EquippedItemInfoDTO;
import umc.puppymode.web.dto.ItemCategoryResponseDto;
import umc.puppymode.web.dto.ItemResponseDto;
import umc.puppymode.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuppyItemServiceImpl implements PuppyItemService {

    private final PuppyItemCategoryRepository categoryRepository;
    private final PuppyItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PuppyCustomizationRepository puppyCustomizationRepository;
    private final PuppyRepository puppyRepository;

    @Override
    public Map<String, Object> getAllCategories() {
        // 전체 카테고리 수
        Long totalCategoryCount = categoryRepository.count();

        List<ItemCategoryResponseDto> categories = categoryRepository.findAll().stream()
                .map(category -> new ItemCategoryResponseDto(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        itemRepository.countByCategory_CategoryId(category.getCategoryId()) // 카테고리 별 아이템 수
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCategoryCount);
        result.put("categories", categories);

        return result;
    }

    @Override
    public Map<String, Object> getItemsByCategory(Long categoryId) {
        // 해당 카테고리의 아이템 목록
        List<PuppyItem> items = itemRepository.findAllByCategory_CategoryId(categoryId);

        List<ItemResponseDto> itemResponseList = items.stream()
                .map(item -> new ItemResponseDto(
                        item.getItemId(),
                        item.getItemName(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", itemResponseList.size());  // 해당 카테고리의 아이템 수
        result.put("items", itemResponseList);

        return result;
    }

    @Override
    public Map<String, Object> purchaseItem(Long categoryId, Long itemId, User user) {
        // 강아지 조회
        Puppy puppy = puppyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다."));

        // 아이템 조회
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new IllegalArgumentException("아이템이 해당 카테고리에 속하지 않습니다.");
        }

        // 이미 구매된 아이템인지 확인
        boolean alreadyPurchased = puppyCustomizationRepository.existsByPuppyAndPuppyItem(puppy, item);
        if (alreadyPurchased) {
            throw new IllegalArgumentException("이미 구매한 아이템입니다.");
        }

        // 포인트 검증
        if (user.getPoints() < item.getPrice()) {
            throw new IllegalArgumentException("잔여 포인트가 부족합니다.");
        }

        // 포인트 차감 및 아이템 구매 처리
        user.setPoints(user.getPoints() - item.getPrice());
        PuppyCustomization customization = new PuppyCustomization();
        customization.setPuppy(puppy);
        customization.setPuppyItem(item);
        customization.setIsEquipped(false); // 기본값은 장착되지 않은 상태
        puppyCustomizationRepository.save(customization);

        // 저장
        userRepository.save(user);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("currentPoint", user.getPoints());
        response.put("itemId", item.getItemId());

        return response;
    }

    @Override
    public Map<String, Object> equipItem(Long categoryId, Long itemId, User user) {
        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다."));

        // 아이템 찾기
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템이 존재하지 않습니다."));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new IllegalArgumentException("아이템이 해당 카테고리에 속하지 않습니다.");
        }

        // 아이템 구매 여부 확인
        PuppyCustomization customization = puppyCustomizationRepository.findByPuppyAndPuppyItem(puppy, item)
                .orElseThrow(() -> new IllegalArgumentException("강아지에게 해당 아이템이 없습니다."));

        // 이미 착용 중인 아이템인지 확인
        if (customization.getIsEquipped()) {
            throw new IllegalArgumentException("이미 착용한 아이템입니다.");
        }

        // 아이템 착용 처리
        customization.setIsEquipped(true);
        puppyCustomizationRepository.save(customization);

        // 강아지 이미지 갱신 (추후에 강아지의 이미지 URL을 업데이트하는 로직을 추가)
        String updatedImageUrl = "";

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("updatedPuppyImageUrl", updatedImageUrl);
        response.put("equippedItemInfo", new EquippedItemInfoDTO(item.getItemId(), item.getItemName()));

        return response;
    }

}
