package umc.puppymode.domain.enums;

import lombok.Getter;

@Getter
public enum FeedingItem {
    CHICKEN("닭 고기", ""),
    BEEF("소 고기", ""),
    SALMON("연어", ""),
    SWEET_POTATO("고구마", ""),
    CARROT("당근", ""),
    PUMPKIN("호박", ""),
    BLUEBERRY("블루베리", ""),
    BANANA("바나나", ""),
    APPLE("사과", ""),
    EGG("달걀", "");

    private final String description;
    private final String imageUrl;

    FeedingItem(String description, String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
