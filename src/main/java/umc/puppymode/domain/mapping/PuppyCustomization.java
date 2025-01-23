package umc.puppymode.domain.mapping;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;
import umc.puppymode.domain.PuppyItemCategory;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PuppyCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puppy_id")
    private Puppy puppy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private PuppyItem puppyItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PuppyItemCategory puppyItemCategory;

    private Boolean isEquipped; // 아이템 장착 여부

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
