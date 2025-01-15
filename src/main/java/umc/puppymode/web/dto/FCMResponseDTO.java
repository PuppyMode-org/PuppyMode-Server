package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCMResponseDTO {

    private boolean validateOnly;
    private FCMResponseDTO.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {

        private FCMResponseDTO.Notification notification;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {

        private String title;
        private String body;
        private String image;
    }

}
