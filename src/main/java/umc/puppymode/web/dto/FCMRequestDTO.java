package umc.puppymode.web.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FCMRequestDTO {

    private String token;
    private String title;
    private String body;
    private String image;

    @Builder(toBuilder = true)
    public FCMRequestDTO(String token, String title, String body, String image) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.image = image;
    }
}
