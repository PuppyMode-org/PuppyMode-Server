package umc.puppymode.web.dto.FCMDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class FCMPlayRequestDTO extends FCMRequestDTO {

    @Builder
    public FCMPlayRequestDTO(String token, String title, String body, String image) {
        super(token, title, body, image);
    }
}
