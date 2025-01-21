package umc.puppymode.domain.mapping;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyItem;

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

    private Boolean isEquipped; // 아이템 장착 여부

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
