package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppleTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}