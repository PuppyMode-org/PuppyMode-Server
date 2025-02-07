package umc.puppymode.service.PuppyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.PuppyItemCategory;
import umc.puppymode.domain.EquippedItemImage;
import umc.puppymode.domain.mapping.PuppyCustomization;
import umc.puppymode.repository.*;
import umc.puppymode.web.dto.PuppyCustomDTO.EquippedItemInfoDTO;
import umc.puppymode.web.dto.PuppyCustomDTO.ItemCategoryResponseDTO;
import umc.puppymode.domain.User;
import umc.puppymode.web.dto.PuppyCustomDTO.ItemResponseDTO;

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
        if (items.isEmpty()) {
            throw new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

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
    @Transactional
    public Map<String, Object> purchaseItem(Long categoryId, Long itemId, Long userId) {
        // 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

        // 아이템 조회
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new GeneralException(ErrorStatus.ITEM_CATEGORY_NOT_FOUND);
        }
        PuppyItemCategory puppyItemCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 도전 과제 보상 아이템 확인
        if (item.getMission_item()) {
            throw new GeneralException(ErrorStatus.NO_PURCHASE_ITEM);
        }

        // 아이템 구매 여부 확인
        boolean isPurchased = puppyCustomizationRepository.existsByPuppyAndPuppyItem(puppy, item);
        if (isPurchased) {
            throw new GeneralException(ErrorStatus.ITEM_ALREADY_PURCHASE);
        }

        // 포인트 검증
        if (user.getPoints() < item.getPrice()) {
            throw new GeneralException(ErrorStatus.POINT_NOT_ENOUGH);
        }

        // 포인트 차감 및 아이템 구매 처리
        user.setPoints(user.getPoints() - item.getPrice());
        PuppyCustomization customization = PuppyCustomization.builder().build();
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
    @Transactional
    public EquippedItemInfoDTO equipItem(Long categoryId, Long itemId, Long userId) {

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

        // 아이템 찾기
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new GeneralException(ErrorStatus.ITEM_CATEGORY_NOT_FOUND);
        }
        PuppyItemCategory puppyItemCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 아이템 구매 여부 확인
        PuppyCustomization customization = puppyCustomizationRepository.findByPuppyAndPuppyItem(puppy, item)
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNPURCHASE_ITEM));

        // 착용 중인 아이템 해제
        List<PuppyCustomization> equippedItems = puppyCustomizationRepository.findByPuppyAndIsEquippedTrue(puppy);
        for (PuppyCustomization equippedItem : equippedItems) {
            equippedItem.setIsEquipped(false);
            puppyCustomizationRepository.save(equippedItem);
        }

        // 아이템 착용 처리
        customization.setIsEquipped(true);
        customization.setUpdatedAt(LocalDateTime.now());
        puppyCustomizationRepository.save(customization);

        // 아이템 착용 이미지
        String equippedImage = equippedItemImageRepository.findByPuppyTypeAndLevelNameAndItemId(
                puppy.getPuppyLevel().getPuppyType(),
                puppy.getPuppyLevel().getLevelName(),
                itemId
        ).map(EquippedItemImage::getImageUrl)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EQUIPPED_IMAGE_NOT_FOUND));

        // 착용한 이미지를 강아지의 이미지 필드에 업데이트
        puppy.setImageUrl(equippedImage);
        puppyRepository.save(puppy);

        return new EquippedItemInfoDTO(item.getItemId(), item.getItemName(), equippedImage);
    }

    @Override
    @Transactional
    public EquippedItemInfoDTO unequipItem(Long categoryId, Long itemId, Long userId) {

        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

        // 아이템 찾기
        PuppyItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 카테고리 검증
        if (!item.getCategory().getCategoryId().equals(categoryId)) {
            throw new GeneralException(ErrorStatus.ITEM_CATEGORY_NOT_FOUND);
        }
        PuppyItemCategory puppyItemCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 아이템 구매 여부 확인
        PuppyCustomization customization = puppyCustomizationRepository.findByPuppyAndPuppyItem(puppy, item)
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNPURCHASE_ITEM));

        // 아이템 착용 여부 확인
        if (customization.getIsEquipped()) {
            customization.setIsEquipped(false);
            puppyCustomizationRepository.save(customization);
        } else {
            throw new GeneralException(ErrorStatus.ITEM_ALREADY_UNEQUIPPED);
        }

        // 아이템 착용 해제 이미지
        String unEquippedImage = puppy.getPuppyLevel().getLevelImageUrl();
        puppy.setImageUrl(unEquippedImage);
        puppyRepository.save(puppy);

        return new EquippedItemInfoDTO(item.getItemId(), item.getItemName(), unEquippedImage);
    }

    @Override
    public Integer getPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return user.getPoints();
    }

    @Override
    public List<ItemResponseDTO> getOwnedItems(Long userId) {
        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

        // 모든 아이템 조회
        List<PuppyItem> items = itemRepository.findAll();

        // 유저가 소유한 아이템 리스트
        List<Long> OwnedItemIds = puppyCustomizationRepository.findByPuppy(puppy).stream()
                .map(customization -> customization.getPuppyItem().getItemId())
                .collect(Collectors.toList());

        List<ItemResponseDTO> itemResponseList = items.stream()
                .filter(item -> OwnedItemIds.contains(item.getItemId())) // 소유한 아이템만 필터링
                .map(item -> new ItemResponseDTO(
                        item.getItemId(),
                        item.getItemName(),
                        item.getPrice(),
                        item.getImageUrl(),
                        OwnedItemIds.contains(item.getItemId()),
                        item.getMission_item()
                ))
                .collect(Collectors.toList());

        return itemResponseList;
    }

    @Override
    public List<EquippedItemInfoDTO> getEquippedItems(Long userId) {
        // 유저의 강아지 찾기
        Puppy puppy = puppyRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_USERS_PUPPY));

        // 구매한 아이템 목록
        List<PuppyCustomization> equippedItems = puppyCustomizationRepository.findByPuppyAndIsEquippedTrue(puppy);

        // 리스트 변환
        List<EquippedItemInfoDTO> equippedItemInfoDTOList = equippedItems.stream()
                .map(customization -> {
                    PuppyItem item = customization.getPuppyItem();
                    // 아이템 착용 이미지
                    String equippedImage = equippedItemImageRepository.findByPuppyTypeAndLevelNameAndItemId(
                                    puppy.getPuppyLevel().getPuppyType(),
                                    puppy.getPuppyLevel().getLevelName(),
                                    item.getItemId()
                            ).map(EquippedItemImage::getImageUrl)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.EQUIPPED_IMAGE_NOT_FOUND));

                    return new EquippedItemInfoDTO(item.getItemId(), item.getItemName(), equippedImage);
                })
                .collect(Collectors.toList());

        return equippedItemInfoDTOList;
    }

}
