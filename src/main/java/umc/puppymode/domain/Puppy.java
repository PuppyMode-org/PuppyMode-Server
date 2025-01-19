package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;
import umc.puppymode.domain.enums.CustomizingItem;

@Entity
@Getter
@Setter
public class Puppy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long puppyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "puppy_level_id")
    private PuppyLevel puppyLevel;

    private String puppyImageUrl;
    private String puppyName;
    private Integer puppyExp;

    @Enumerated(EnumType.STRING)
    private CustomizingItem puppyItem;

    public void updatePuppyName(String puppyName) {
        this.puppyName = puppyName;
    }

    public void updatePuppyExp(Integer puppyExp) {
        this.puppyExp += puppyExp;
        if (this.puppyExp >= puppyLevel.getLevelMaxEXP()) {
            PuppyLevel nextLevel = puppyLevel.getNextLevel();
            if (nextLevel != null) {
                this.puppyLevel = nextLevel;
            } // nextLevel이 null인 경우 현재 레벨이 최대 레벨
        }
    }
}
