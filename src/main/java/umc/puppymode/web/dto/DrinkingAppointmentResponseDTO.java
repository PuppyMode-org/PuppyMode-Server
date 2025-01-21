package umc.puppymode.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import umc.puppymode.domain.enums.AppointmentStatus;

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

    @Getter
    @AllArgsConstructor
    public static class AppointmentStatusResultDTO {
        private Long appointmentId;

        @Getter(AccessLevel.NONE)
        private boolean isActive;

        public boolean getIsActive() {
            return isActive;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RescheduleResultDTO {
        private Long appointmentId;
        private LocalDateTime rescheduledTime;
    }

    @Getter
    @AllArgsConstructor
    public static class CompletedResultDTO {
        private LocalDateTime completedTime;
        private AppointmentStatus appointmentStatus;
    }
}
