package umc.puppymode.web.dto;

import lombok.*;

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
}
