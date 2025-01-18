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
    }

    // Exp 변경 시마다 puppyLevel.MaxExp 값을 넘는지 체크한 후, 넘는다면 puppyLevel 을 바꿔주는 메서드 추가하면 좋을 것 같음.
    // updatePuppyExp 메서드 내부에, puppyExp를 증가시킨 후 해당 메서드를 넣어주면 될듯.
}
