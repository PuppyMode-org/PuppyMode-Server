package umc.puppymode.web.dto.FCMDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FCMPlayResponseDTO {

    private String message;

    public FCMPlayResponseDTO(String message) {
        this.message = message;
    }
}
