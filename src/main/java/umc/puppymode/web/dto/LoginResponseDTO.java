package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String jwt;
    private LoginUserInfoDTO userInfo;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginUserInfoDTO {
        private Long userId;
        private String email;
        private String username;
        private Boolean isNewUser;
    }
}