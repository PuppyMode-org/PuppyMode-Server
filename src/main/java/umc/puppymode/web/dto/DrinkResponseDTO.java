package umc.puppymode.web.dto;

import lombok.*;

import java.util.List;

public class DrinkResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HangoverResponseDTO {
        private Long hangoverId;
        private String hangoverName;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponseDTO {
        private Long categoryId;
        private String categoryName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrinksResponseDTO {
        private Long itemId;
        private String itemName;
        private Float alcoholPercentage;
        private Integer volumeMl;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrinkItemsByCategoryResponseDTO {
        private Long categoryId;
        private String categoryName;
        private List<DrinksResponseDTO> items;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrinksRecordResponseDTO {
        private String message;
        private String feedImageUrl;
        private String feedType;
        private Long puppyLevel;
        private String puppyName;
        private Integer puppyPercent;
    }
}
