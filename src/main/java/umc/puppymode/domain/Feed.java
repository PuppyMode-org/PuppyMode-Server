package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.puppymode.domain.common.BaseEntity;
import umc.puppymode.domain.enums.FeedingItem;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feed extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    @OneToOne
    @JoinColumn(name = "drink_history_id")
    private DrinkHistory drinkHistory;

    private String feedingType;
    private String feedImageUrl;
}
