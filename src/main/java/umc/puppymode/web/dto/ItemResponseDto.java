package umc.puppymode.web.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponseDto {
    private Long itemId; // 아이템 ID
    private String name; // 아이템 이름
    private Integer price; // 아이템 가격
}
