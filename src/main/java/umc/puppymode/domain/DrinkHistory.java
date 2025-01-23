package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.puppymode.domain.common.BaseEntity;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrinkHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drinkHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "drink_history_hangover",
            joinColumns = @JoinColumn(name = "history_id"),
            inverseJoinColumns = @JoinColumn(name = "hangover_id")
    )
    private List<HangoverItem> hangovers;

    private LocalDate drinkDate;
    private Float drinkAmount;
}
