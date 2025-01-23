package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemCategoryResponseDto {
    private Long categoryId; // 카테고리 ID
    private String name; // 카테고리 이름
    private Long itemCount; // 아이템 수
}

