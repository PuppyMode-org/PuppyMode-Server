package umc.puppymode.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRequestDTO {
    @NotNull(message = "위도 값은 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도 값은 필수입니다.")
    private Double longitude;
}