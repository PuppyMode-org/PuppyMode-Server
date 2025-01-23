package umc.puppymode.web.dto.MainPuppyDTO;

import lombok.Builder;
import lombok.Getter;

public class MainPuppyResDTO {

    @Getter
    @Builder
    // 온보딩 화면에 보여지는 랜덤선택된 강아지의 정보
    public static class RandomPuppyViewDTO {
        private Long userId;
        private String puppyType;
        private String puppyImageUrl;
    }

    @Getter
    @Builder
    // 메인 화면에 보여지는 사용자의 강아지 정보
    public static class UserPuppyViewDTO {
        private Long puppyId;
        private String puppyName;
        private Integer level;
        private String levelName;
        private String imageUrl;
        private Integer levelMinExp; // 해당 레벨의 최소 경험치 값 (퍼센트 계산에 사용)
        private Integer levelMaxExp; // 해당 레벨의 최대 경험치 값 (퍼센트 계산 및 레벨업에 사용)
        private Integer puppyExp; // 강아지의 현재 경험치 값
        // customization DTO는 커스텀 관련 기능이 추가된 후 넣을 예정입니다.
    }

    @Getter
    @Builder
    // 강아지 놀아주기 실행 후 반환되는 값
    public static class PlayResDTO {
        private Integer level;
        private Integer levelMinExp; // 해당 레벨의 최소 경험치 값 (퍼센트 계산에 사용)
        private Integer levelMaxExp; // 해당 레벨의 최대 경험치 값 (퍼센트 계산 및 레벨업에 사용)
        private Integer puppyExp; // 강아지의 현재 경험치 값
    }
}
