package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppleLoginRequestDTO {
    private String authorizationCode;
    private String identityToken;
    private String username;
    private String fcmToken;
}
