package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class PuppyItemCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName; // 카테고리 이름
}

