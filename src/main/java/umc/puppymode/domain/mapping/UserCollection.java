package umc.puppymode.domain.mapping;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.Collection;
import umc.puppymode.domain.User;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class UserCollection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCollectionId;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 현재 숙취 횟수
    private Integer currentNum;

    // 달성 여부
    private boolean isCompleted;
}
