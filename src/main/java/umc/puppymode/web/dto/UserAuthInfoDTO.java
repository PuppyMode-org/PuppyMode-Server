package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.puppymode.domain.enums.AuthProvider;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthInfoDTO {
    private String userAuthId;       // OAuth 제공자의 고유 ID
    private Long userId;
    private String email;
    private String username;
    private AuthProvider authProvider; // OAuth 제공자 정보 (KAKAO, APPLE 등)
}
