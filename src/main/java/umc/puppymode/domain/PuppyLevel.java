package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;
import umc.puppymode.domain.enums.PuppyType;

@Entity
@Getter
@Setter
public class PuppyLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long puppyLevelId;

    @Enumerated(EnumType.STRING)
    private PuppyType puppyType;

    @ManyToOne
    @JoinColumn(name = "next_level_id")
    private PuppyLevel nextLevel;

    private Integer puppyLevel;
    private String levelName;
    private String levelImageUrl;
    private Integer levelMinExp;
    private Integer levelMaxExp;
}
