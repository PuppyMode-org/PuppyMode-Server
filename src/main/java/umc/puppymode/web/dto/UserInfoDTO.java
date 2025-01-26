package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDTO {
    private Long userId;
    private String username;
    private String email;
}
