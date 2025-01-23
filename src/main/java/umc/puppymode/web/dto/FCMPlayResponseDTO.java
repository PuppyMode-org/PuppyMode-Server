package umc.puppymode.web.dto;

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
