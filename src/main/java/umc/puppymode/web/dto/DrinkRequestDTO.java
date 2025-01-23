package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DrinkRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrinkRecordDTO {
        LocalDate drinkDate;
        List<Long> hangoverOptions;
        List<AlcoholTolerance> alcoholTolerance;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlcoholTolerance {
        private Long drinkCategoryId;
        private Long drinkItemId;
        private Float value;
        private String unit;
    }
}
