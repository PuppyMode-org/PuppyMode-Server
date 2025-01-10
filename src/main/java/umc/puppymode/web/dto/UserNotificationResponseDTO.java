package umc.puppymode.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserNotificationResponseDTO {

    private Boolean isEnabled;
}
