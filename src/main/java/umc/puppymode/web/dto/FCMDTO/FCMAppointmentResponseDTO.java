package umc.puppymode.web.dto.FCMDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FCMAppointmentResponseDTO {

    private String message;
    private List<Long> appointmentIds;

    public FCMAppointmentResponseDTO(String message, Long appointmentId) {
        this.message = message;
        this.appointmentIds = List.of(appointmentId);
    }

    public FCMAppointmentResponseDTO(String message, List<Long> appointmentIds) {
        this.message = message;
        this.appointmentIds = appointmentIds;
    }
}
