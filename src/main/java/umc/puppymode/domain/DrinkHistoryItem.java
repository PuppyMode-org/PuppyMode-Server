package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrinkHistoryItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_history_id", nullable = false)
    private DrinkHistory history;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private DrinkItem item;

    private String unit;
    private Float value;
    private Float safetyValue;
    private Float maxValue;

}
