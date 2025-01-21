package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import umc.puppymode.domain.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PuppyItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PuppyItemCategory category; // 카테고리 ID

    private String itemName; // 아이템 이름
    private String imageUrl; // 아이템 이미지 URL
    private Integer price; // 아이템 가격
}
