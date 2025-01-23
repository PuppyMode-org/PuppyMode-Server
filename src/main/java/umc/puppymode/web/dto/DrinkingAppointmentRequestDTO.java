package umc.puppymode.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import umc.puppymode.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;


public class DrinkingAppointmentRequestDTO {

    @Getter
    public static class AppointmentDTO {
        //날짜가 오늘로 고정인지, 아니면 변동 가능한 지에 따라 추후 수정가능성 있습니다.
        @NotNull(message = "날짜 및 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime dateTime;

        @NotNull(message = "위도 값은 필수입니다.")
        @Min(value = -90, message = "위도 값은 -90 이상이어야 합니다.")
        @Max(value = 90, message = "위도 값은 90 이하여야 합니다.")
        private Double latitude;

        @NotNull(message = "경도 값은 필수입니다.")
        @Min(value = -180, message = "경도 값은 -180 이상이어야 합니다.")
        @Max(value = 180, message = "경도 값은 180 이하여야 합니다.")
        private Double longitude;

        @NotBlank(message = "주소는 필수입니다.")
        private String address;

        @NotBlank(message = "장소 이름은 필수입니다.")
        @Size(max = 255, message = "장소 이름은 255자 이하여야 합니다.")
        private String locationName;

        // userId는 시큐리티 구현 후 토큰 발급이 되면 추후 수정.
        @NotNull(message = "유저 ID는 필수 입력 사항입니다.")
        private Long userId;
    }

    @Getter
    public static class RescheduleAppointmentRequestDTO {
        // 술 약속 설정과 마찬가지로 오늘 고정인지, 날짜 자체 변동이 가능한지에 따라 수정가능성 있습니다.
        @NotNull(message = "날짜 및 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime dateTime;
    }

    @Getter
    public static class CompletedAppointmentRequestDTO {
        @NotNull(message = "술 약속 상태는 필수 입력 사항입니다.")
        private AppointmentStatus status;
    }

    @Getter
    public static class StartAppointmentRequestDTO {
        @NotNull(message = "위도 값은 필수입니다.")
        @Min(value = -90, message = "위도 값은 -90 이상이어야 합니다.")
        @Max(value = 90, message = "위도 값은 90 이하여야 합니다.")
        private Double latitude;

        @NotNull(message = "경도 값은 필수입니다.")
        @Min(value = -180, message = "경도 값은 -180 이상이어야 합니다.")
        @Max(value = 180, message = "경도 값은 180 이하여야 합니다.")
        private Double longitude;
    }
}


