package umc.puppymode.service.PuppyService;

import umc.puppymode.web.dto.PuppyCustomDTO.EquippedItemInfoDTO;
import umc.puppymode.web.dto.PuppyCustomDTO.ItemResponseDTO;

import java.util.List;
import java.util.Map;

public interface PuppyItemService {
    Map<String, Object> getAllCategories();
    Map<String, Object> getItemsByCategory(Long categoryId, Long userId);
    Map<String, Object> purchaseItem(Long categoryId, Long itemId, Long userId);
    EquippedItemInfoDTO equipItem(Long categoryId, Long itemId, Long userId);
    EquippedItemInfoDTO unequipItem(Long categoryId, Long itemId, Long userId);
    Integer getPoints(Long userId);
    List<ItemResponseDTO> getOwnedItems(Long userId);
    List<EquippedItemInfoDTO> getEquippedItems(Long userId);
}