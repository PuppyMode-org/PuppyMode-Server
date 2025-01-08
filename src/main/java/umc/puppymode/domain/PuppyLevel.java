package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class PuppyLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long puppyLevelId;

    @ManyToOne
    @JoinColumn(name = "puppy_id")
    private Puppy puppy;

    private Integer puppyLevel;
    private String puppyGrowth;
}
