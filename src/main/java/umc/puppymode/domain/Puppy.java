package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;
import umc.puppymode.domain.enums.CustomizingItem;
import umc.puppymode.domain.enums.PuppyType;

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

    @Enumerated(EnumType.STRING)
    private PuppyType puppyType;

    private String puppyImageUrl;
    private String puppyName;

    @Enumerated(EnumType.STRING)
    private CustomizingItem puppyItem;
}
