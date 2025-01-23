package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;
import umc.puppymode.domain.enums.PuppyType;

@Entity
@Getter
@Setter
@Table(name = "equipped_item_image")
public class EquippedItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Enumerated(EnumType.STRING)
    private PuppyType puppyType; // 강아지 타입

    private String levelName; // 강아지 레벨 이름

    private Long itemId; // 아이템 아이디

    private String imageUrl; // 착용 이미지 URL
}


