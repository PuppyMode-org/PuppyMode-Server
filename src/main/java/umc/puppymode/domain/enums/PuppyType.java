package umc.puppymode.domain.enums;

public enum PuppyType {
    // 포메라니안 타입
    POMERANIAN_BABY("아기사자 포메라니안"),
    POMERANIAN_YOUTH("꼬마사자 포메라니안"),
    POMERANIAN_ADULT("작은사자 포메라니안"),

    // 웰시코기 타입
    WELSH_CORGI_BABY("땅콩 웰시코기"),
    WELSH_CORGI_YOUTH("빵떡 웰시코기"),
    WELSH_CORGI_ADULT("핫도그 웰시코기"),

    // 비숑 타입
    BICHON_BABY("눈송이 비숑"),
    BICHON_YOUTH("솜사탕 비숑"),
    BICHON_ADULT("털뭉치 비숑");

    private final String description;

    PuppyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
