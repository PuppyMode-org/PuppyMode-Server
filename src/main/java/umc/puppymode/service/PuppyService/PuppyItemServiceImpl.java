package umc.puppymode.service.PuppyService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.PuppyItemCategory;
import umc.puppymode.domain.EquippedItemImage;
import umc.puppymode.domain.mapping.PuppyCustomization;
import umc.puppymode.repository.*;
import umc.puppymode.web.dto.EquippedItemInfoDTO;
import umc.puppymode.web.dto.ItemCategoryResponseDTO;
import umc.puppymode.domain.User;
import umc.puppymode.web.dto.ItemResponseDTO;

import java.time.LocalDateTime;
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
    private final EquippedItemImageRepository equippedItemImageRepository;


    @Override
    public Map<String, Object> getAllCategories() {
        // 전체 카테고리 수
        Long totalCategoryCount = categoryRepository.count();

        List<ItemCategoryResponseDTO> categories = categoryRepository.findAll().stream()
                .map(category -> new ItemCategoryResponseDTO(
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
    public Map<String, Object> getItemsByCategory(Long categoryId, Long userId) {
        // 해당 카테고리의 아이템 목록
        List<PuppyItem> items = itemRepository.findAllByCategory_CategoryId(categoryId);

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다."));

        // 유저가 구매한 아이템 리스트
        List<Long> purchasedItemIds = puppyCustomizationRepository.findByPuppy(puppy).stream()
                .map(customization -> customization.getPuppyItem().getItemId())
                .collect(Collectors.toList());

        List<ItemResponseDTO> itemResponseList = items.stream()
                .map(item -> new ItemResponseDTO(
                        item.getItemId(),
                        item.getItemName(),
                        item.getPrice(),
                        item.getImageUrl(),
                        purchasedItemIds.contains(item.getItemId()), // 구매 여부
                        item.getMission_item()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", itemResponseList.size());  // 해당 카테고리의 아이템 수
        result.put("items", itemResponseList);

        return result;
    }

    @Override
    public Map<String, Object> purchaseItem(Long categoryId, Long itemId, Long userId) {
        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다."));

        // 아이템 조회
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new IllegalArgumentException("아이템이 해당 카테고리에 속하지 않습니다.");
        }
        PuppyItemCategory puppyItemCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        // 도전 과제 보상 아이템 확인
        if (item.getMission_item()) {
            throw new IllegalArgumentException("도전 과제로 얻는 아이템은 구매할 수 없습니다.");
        }

        // 아이템 구매 여부 확인
        boolean isPurchased = puppyCustomizationRepository.existsByPuppyAndPuppyItem(puppy, item);
        if (isPurchased) {
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
        customization.setPuppyItemCategory(puppyItemCategory);
        customization.setIsEquipped(false); // 기본값은 장착되지 않은 상태
        customization.setCreatedAt(LocalDateTime.now());
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
    public Map<String, Object> equipItem(Long categoryId, Long itemId, Long userId) {
        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("강아지가 존재하지 않습니다."));

        // 아이템 찾기
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템이 존재하지 않습니다."));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new IllegalArgumentException("아이템이 해당 카테고리에 속하지 않습니다.");
        }
        PuppyItemCategory puppyItemCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        // 아이템 구매 여부 확인
        PuppyCustomization customization = puppyCustomizationRepository.findByPuppyAndPuppyItem(puppy, item)
                .orElseThrow(() -> new IllegalArgumentException("구매하지 않은 아이템입니다."));


        // 같은 카테고리에서 현재 착용 중인 아이템 해제
        PuppyCustomization currentEquippedItem = puppyCustomizationRepository.findByPuppyAndPuppyItemCategoryAndIsEquippedTrue(puppy, puppyItemCategory)
                .orElse(null);
        if (currentEquippedItem != null) {
            currentEquippedItem.setIsEquipped(false);
            puppyCustomizationRepository.save(currentEquippedItem);
        }

        // 아이템 착용 처리
        customization.setIsEquipped(true);
        customization.setUpdatedAt(LocalDateTime.now());
        puppyCustomizationRepository.save(customization);

        // 아이템 착용 이미지
        String updatedImageUrl = equippedItemImageRepository.findByPuppyTypeAndLevelNameAndItemId(
                puppy.getPuppyLevel().getPuppyType(),
                puppy.getPuppyLevel().getLevelName(),
                itemId
        ).map(EquippedItemImage::getImageUrl)
                .orElseThrow(() -> new IllegalArgumentException("착용 이미지가 존재하지 않습니다."));

        // 응답 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("updatedPuppyImageUrl", updatedImageUrl);
        result.put("equippedItemInfo", new EquippedItemInfoDTO(item.getItemId(), item.getItemName()));

        return result;
    }

}
