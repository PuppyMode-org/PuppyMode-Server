package umc.puppymode.domain.enums;

public enum CustomizingItem {
    // 모자 카테고리
    HAT_RAINBOW("머리 위에 무지개"),
    HAT_STAR_ONE("머리 위에 별 하나"),
    HAT_STAR_THREE("머리 위에 별 세개"),
    HAT_ROCK("돌 모자"),
    HAT_PANDA_HEADBAND("판다 머리띠"),

    // 옷 카테고리
    CLOTHING_BANDAGE("붕대"),
    CLOTHING_MUANG("멍 분장"),
    CLOTHING_SANTA("산타 옷"),
    CLOTHING_LUXURY("명품 강아지 옷"),
    CLOTHING_GRANDMA_VEST("할머니 조끼"),

    // 집 카테고리
    HOUSE_STONE_BED("별 다섯개 짜리 돌침대"),
    HOUSE_TOILET("좌변기"),
    HOUSE_BLANKET("이불"),
    HOUSE_CUTE("예쁜 강아지 집"),
    HOUSE_TENT("텐트 감성의 집"),

    // 바닥 카테고리
    FLOOR_RAINBOW("무지개 카페트"),
    FLOOR_SEA("무지개 바다"),
    FLOOR_WATER("정수기 생수통"),
    FLOOR_KOTATSU("코타츠"),
    FLOOR_CUTE_RUG("귀여운 러그"),

    // 장난감 카테고리
    TOY_TOILET_PAPER("두루마리 휴지"),
    TOY_MEDICINE("소화제"),
    TOY_IV("링거"),
    TOY_SOJU("소주병"),
    TOY_WATER_BOTTLE("1.5L 생수통");

    private final String description;

    CustomizingItem(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
