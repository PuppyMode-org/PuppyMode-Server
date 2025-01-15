package umc.puppymode.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum RandomMessages {
    DRINK_WATER("물은 자주 마시는 게 좋아요!"),
    AVOID_HANGOVER("주인님 숙취를 겪으실 수 있으니 조금만 드세요!"),
    DRINK_LIMIT("주인님 너무 많이 드신 건 아니죠?"),
    DRINK_SLOWLY("너무 급하게 마시지 않는 게 좋아요!"),
    DRINK_TIME_ELAPSED("벌써 음주하신 지 %s시간 지났어요!");

    private final String message;
    private static final List<RandomMessages> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final Random RANDOM = new Random();

    RandomMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static RandomMessages getRandom() {
        return VALUES.get(RANDOM.nextInt(VALUES.size()));
    }

    public static String getDrinkTimeElapsedMessage(long minutesElapsed) {
        return String.format(DRINK_TIME_ELAPSED.message, minutesElapsed);
    }
}
