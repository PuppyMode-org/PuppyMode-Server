package umc.puppymode.service.DrinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
}
