package umc.puppymode.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class KakaoFriendsResponseDTO {

    private List<Friend> elements; // 친구 목록 배열
    private int total_count; // 전체 친구 수
    private int favorite_count; // 즐겨찾기한 친구 수

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Friend {
        private Long id; // authId
        private String uuid;
        private boolean favorite;
        private String profile_nickname;
        private String profile_thumbnail_image;
    }
}
