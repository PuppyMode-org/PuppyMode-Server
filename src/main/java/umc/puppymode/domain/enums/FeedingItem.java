package umc.puppymode.domain.enums;

import lombok.Getter;

@Getter
public enum FeedingItem {
    CHICKEN("닭 고기", "https://d1le4wcgenmery.cloudfront.net/174c66cd-dfa5-48da-85d1-b955fbf639b3주량 기록 완료_닭고기.svg"),
    BEEF("소 고기", "https://d1le4wcgenmery.cloudfront.net/3efde3a2-a2da-4b23-9a5d-5de9ffcebe28주량 기록 완료_소고기.svg"),
    SALMON("연어", "https://d1le4wcgenmery.cloudfront.net/ae12e776-8fe2-460a-8492-804257f2164d주량 기록 완료_연어.svg"),
    SWEET_POTATO("고구마", "https://d1le4wcgenmery.cloudfront.net/ea5b83d7-aa71-403b-8207-0e37a09303e6주량 기록 완료_고구마.svg"),
    CARROT("당근", "https://d1le4wcgenmery.cloudfront.net/ae55b2f3-b1be-4755-99cb-6129f027a758주량 기록 완료_당근.svg"),
    PUMPKIN("호박", "https://d1le4wcgenmery.cloudfront.net/6b8b263c-4c95-4c47-b648-481c74fa6dd9주량 기록 완료_호박.svg"),
    BLUEBERRY("블루베리", "https://d1le4wcgenmery.cloudfront.net/dfd7a099-7dc2-4de1-843a-0bceac23571d주량 기록 완료_블루베리.svg"),
    BANANA("바나나", "https://d1le4wcgenmery.cloudfront.net/e6ad3dcc-500b-4cf9-99c6-6477e985c74c주량 기록 완료_바나나.svg"),
    APPLE("사과", "https://d1le4wcgenmery.cloudfront.net/1e3d8b07-5f5a-44b5-8c9d-3f962bda6439주량 기록 완료_사과.svg"),
    EGG("달걀", "https://d1le4wcgenmery.cloudfront.net/a912439f-4829-45de-8b79-d5b16899a955주량 기록 완료_달걀.svg");

    private final String description;
    private final String imageUrl;

    FeedingItem(String description, String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
