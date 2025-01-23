package umc.puppymode.web.dto;

import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
public class FCMAppointmentRequestDTO extends FCMRequestDTO {

    private Long appointmentId;

    public FCMAppointmentRequestDTO() {
    }

    @Builder
    public FCMAppointmentRequestDTO(String token, String title, String body, String image,
                                    Long appointmentId) {
        super(token, title, body, image);
        this.appointmentId = appointmentId;
    }
}
