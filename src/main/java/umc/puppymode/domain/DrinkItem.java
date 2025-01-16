package umc.puppymode.domain;

import umc.puppymode.domain.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrinkItem  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private DrinkCategory category;

    private String itemName;

    private Float alcoholPercentage;

    private Integer volumeMl;

    private String imageUrl;
}
