package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EquippedItemInfoDTO {
    private Long itemId;
    private String itemName;
}