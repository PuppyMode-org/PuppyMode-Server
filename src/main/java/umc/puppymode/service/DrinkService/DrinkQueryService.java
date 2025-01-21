package umc.puppymode.service.DrinkService;

import umc.puppymode.web.dto.DrinkResponseDTO.*;

import java.util.List;

public interface DrinkQueryService {
   List<HangoverResponseDTO> getAllHangovers();
   List<CategoryResponseDTO> getAllDrinkCategories();
   DrinkItemsByCategoryResponseDTO getAllDrinkItemsByCategory(Long categoryId);
}
