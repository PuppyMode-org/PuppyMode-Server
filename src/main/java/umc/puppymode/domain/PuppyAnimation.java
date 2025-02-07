package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.puppymode.domain.enums.AnimationType;
import umc.puppymode.domain.enums.PuppyType;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "puppy_animation")
public class PuppyAnimation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long animationId;

    @Enumerated(EnumType.STRING)
    private AnimationType animationType; // 애니메이션 종류

    @Enumerated(EnumType.STRING)
    private PuppyType puppyType; // 강아지 타입

    private String levelName; // 강아지 레벨 이름

    @ManyToOne
    @JoinColumn(name = "item_id")
    private PuppyItem item; // 착용 아이템

    @OneToMany(mappedBy = "animation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuppyAnimationImage> animationImages; // 애니메이션 이미지 리스트
}
