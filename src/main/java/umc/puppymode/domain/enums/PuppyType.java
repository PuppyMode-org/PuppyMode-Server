package umc.puppymode.domain.enums;

import lombok.Getter;

@Getter
public enum PuppyType {

    POMERANIAN("포메라니안", "https://example.com/pomeranian_baby.jpg"),
    WELSH_CORGI("웰시코기", "https://example.com/welshCorgi_baby.jpg"),
    BICHON("비숑 프리제", "https://example.com/bichon_baby.jpg");

    private final String type;
    private final String imageUrl;

    PuppyType(String type, String imageUrl) {
        this.type = type;
        this.imageUrl = imageUrl;
    }
}
