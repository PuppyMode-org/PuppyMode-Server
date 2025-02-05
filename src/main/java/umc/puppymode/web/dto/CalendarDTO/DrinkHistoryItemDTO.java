package umc.puppymode.web.dto.CalendarDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class DrinkHistoryItemDTO {
    private String itemName;
    private String unit;
    private Float value;
    private Float safetyValue;
    private Float maxValue;

    public DrinkHistoryItemDTO(String itemName, String unit, Float value, Float safetyValue, Float maxValue){
        this.itemName = itemName;
        this.unit = unit;
        this.value = value;
        this.safetyValue = safetyValue;
        this.maxValue = maxValue;
    }
}