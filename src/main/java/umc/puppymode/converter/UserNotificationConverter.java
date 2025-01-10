package umc.puppymode.converter;

import org.springframework.stereotype.Component;
import umc.puppymode.domain.mapping.UserNotification;
import umc.puppymode.web.dto.UserNotificationResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserNotificationConverter {

    public UserNotificationResponseDTO toResponseDto(UserNotification userNotification) {
        return UserNotificationResponseDTO.builder()
                .isEnabled(userNotification.getIsEnabled())
                .build();
    }

    public List<UserNotificationResponseDTO> toResponseDtoList(List<UserNotification> userNotifications) {
        return userNotifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}
