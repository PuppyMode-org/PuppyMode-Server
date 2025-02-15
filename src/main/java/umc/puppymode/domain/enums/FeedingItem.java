package umc.puppymode.domain.enums;

import lombok.Getter;

@Getter
public enum FeedingItem {
    CHICKEN("닭 고기", "https://d1le4wcgenmery.cloudfront.net/8827837e-0403-4d5f-a38e-cf2b9ac9e6b3chicken.png"),
    BEEF("소 고기", "https://d1le4wcgenmery.cloudfront.net/b8747c5e-ce61-4439-a4d8-f5065bf98abdbeef.png"),
    SALMON("연어", "https://d1le4wcgenmery.cloudfront.net/a124080f-68fe-4e3b-bdad-5008c6b1a979salmon.png"),
    SWEET_POTATO("고구마", "https://d1le4wcgenmery.cloudfront.net/0a4f606f-c3ee-4add-8ee2-cb3a717be156sweet_potato.png"),
    CARROT("당근", "https://d1le4wcgenmery.cloudfront.net/1276f7de-201c-4cca-899e-a37b2907e359carrot.png"),
    PUMPKIN("호박", "https://d1le4wcgenmery.cloudfront.net/e6f613fd-5e62-44e9-b5d0-4126c04eb778pumpkin.png"),
    BLUEBERRY("블루베리", "https://d1le4wcgenmery.cloudfront.net/1bd9ad6e-fab4-442a-a11a-43e401ceb357blueberry.png"),
    BANANA("바나나", "https://d1le4wcgenmery.cloudfront.net/d432e9ab-a6ec-4f60-8ea7-ccb51790f49bbanana.png"),
    APPLE("사과", "https://d1le4wcgenmery.cloudfront.net/0086384e-711b-413f-8b90-09023581be39apple.png"),
    EGG("달걀", "https://d1le4wcgenmery.cloudfront.net/27fbe96e-4e0d-463b-84ce-381c3fe86a10egg.png");

    private final String description;
    private final String imageUrl;

    FeedingItem(String description, String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
