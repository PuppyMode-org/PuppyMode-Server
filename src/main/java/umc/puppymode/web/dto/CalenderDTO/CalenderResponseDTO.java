package umc.puppymode.web.dto.CalenderDTO;

import lombok.*;

import java.util.List;


public class CalenderResponseDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalenderDetailDTO {
        private Long drinkHistoryId;
        private String drinkDate;
        private Float drinkAmount;

        private List<DrinkHistoryItemDTO> drinkItems;
        private FeedDTO feed;
    }

}
