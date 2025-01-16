package umc.puppymode.service.PuppyService;

import umc.puppymode.domain.User;
import umc.puppymode.web.dto.ItemCategoryResponseDto;
import umc.puppymode.web.dto.ItemResponseDto;

import java.util.List;
import java.util.Map;

public interface PuppyItemService {
    Map<String, Object> getAllCategories();
    Map<String, Object> getItemsByCategory(Long categoryId);
    Map<String, Object> purchaseItem(Long categoryId, Long itemId, User user);
    Map<String, Object> equipItem(Long categoryId, Long itemId, User user);
}