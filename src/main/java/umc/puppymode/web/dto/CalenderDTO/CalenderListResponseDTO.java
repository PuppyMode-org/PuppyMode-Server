package umc.puppymode.web.dto.CalenderDTO;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class CalenderListResponseDTO {
    private Long drinkHistoryId;
    private LocalDate drinkDate;

    // 명시적인 생성자 추가 (JPQL에서 사용할 수 있도록)
    public CalenderListResponseDTO(Long drinkHistoryId, LocalDate drinkDate) {
        this.drinkHistoryId = drinkHistoryId;
        this.drinkDate = drinkDate;
    }
}
