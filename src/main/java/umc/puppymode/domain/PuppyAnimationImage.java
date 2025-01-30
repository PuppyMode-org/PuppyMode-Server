package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "puppy_animation_image")
public class PuppyAnimationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animation_id")
    private PuppyAnimation animation; // 연결된 애니메이션

    private int frameOrder; // 프레임 순서

    private String imageUrl; // 애니메이션 이미지 URL
}
