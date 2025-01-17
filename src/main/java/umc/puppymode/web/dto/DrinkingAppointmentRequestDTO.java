package umc.puppymode.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;


public class DrinkingAppointmentRequestDTO {

    @Getter
    public static class AppointmentDTO {
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

        @NotNull(message = "유저 ID는 필수 입력 사항입니다.")
        private Long userId;
    }
}


