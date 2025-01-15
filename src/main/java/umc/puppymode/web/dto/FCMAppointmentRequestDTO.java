package umc.puppymode.web.dto;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
public class FCMAppointmentRequestDTO extends FCMRequestDTO {
    private Double targetLatitude;
    private Double targetLongitude;

    @Builder
    public FCMAppointmentRequestDTO(String token, String title, String body, String image,
                                    Double targetLatitude, Double targetLongitude) {
        super(token, title, body, image);
        this.targetLatitude = targetLatitude;
        this.targetLongitude = targetLongitude;
    }
}

