package umc.puppymode.service.PuppyService;

import java.util.Map;

public interface PuppyItemService {
    Map<String, Object> getAllCategories();
    Map<String, Object> getItemsByCategory(Long categoryId, Long userId);
    Map<String, Object> purchaseItem(Long categoryId, Long itemId, Long userId);
    Map<String, Object> equipItem(Long categoryId, Long itemId, Long userId);
}