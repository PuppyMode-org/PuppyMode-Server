package umc.puppymode.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DrinkingAppointmentResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentResultDTO{
        private Long appointmentId; // 약속 ID
        private LocalDateTime dateTime; // 약속 날짜 및 시간
        private String address; // 약속 장소
        private String status;
    }

    @Getter
    @Builder
    public static class AppointmentSimpleDTO {
        private Long appointmentId;
        private LocalDateTime dateTime;
        private String address;
        private String status;
    }

    @Getter
    @AllArgsConstructor
    public static class AppointmentListResultDTO {
        private long totalCount;
        private List<AppointmentSimpleDTO> appointments;
    }
}
