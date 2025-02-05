package umc.puppymode.web.dto.CalenderDTO;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class CalenderListResponseDTO {
    private LocalDate drinkDate;
    private String status;
    private Long drinkHistoryId;
    private String historyStatus;
    private Long appointmentId;
    private String appointmentTime;

    // 명시적인 생성자 추가 (JPQL에서 사용할 수 있도록)
    public CalenderListResponseDTO(LocalDate drinkDate, String status, Long drinkHistoryId, String historyStats, Long appointmentId, String appointmentTime) {
        this.drinkDate = drinkDate;
        this.status = status;
        this.drinkHistoryId = drinkHistoryId;
        this.historyStatus = historyStats;
        this.appointmentId = appointmentId;
        this.appointmentTime = appointmentTime;
    }
}
