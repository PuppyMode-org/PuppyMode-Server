package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrinkCategory  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<DrinkItem> items;
}
