package umc.puppymode.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private LoginUserInfo userInfo;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginUserInfo {
        @JsonIgnore
        private Long userId;

        private String username;
        private Boolean isNewUser;
    }
}
