package umc.puppymode.web.dto.CalendarDTO;

import lombok.*;
import umc.puppymode.domain.HangoverItem;
import umc.puppymode.web.dto.DrinkResponseDTO;

import java.util.List;


public class CalendarResponseDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarDetailDTO {
        private Long drinkHistoryId;
        private String drinkDate;
        private Float drinkAmount;

        private List<DrinkHistoryItemDTO> drinkItems;
        private FeedDTO feed;
        private List<DrinkResponseDTO.HangoverResponseDTO> hangoverItems;
    }

}
