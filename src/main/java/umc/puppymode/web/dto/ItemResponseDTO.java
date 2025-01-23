package umc.puppymode.web.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponseDTO {
    private Long itemId; // 아이템 ID
    private String name; // 아이템 이름
    private Integer price; // 아이템 가격
    private String image_url; // 아이템 이미지
    private Boolean isPurchased; // 아이템 구매 여부
    private Boolean mission_item; // 도전 과제 보상 아이템 여부
}