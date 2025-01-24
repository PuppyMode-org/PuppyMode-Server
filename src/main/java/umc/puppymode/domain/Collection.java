package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class Collection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collectionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hangover_id")
    // 컬렉션 달성 조건에 해당하는 숙취 유형
    private HangoverItem hangoverItem;

    @ManyToOne
    @JoinColumn(name = "puppy_item_id")
    // 컬렉션 조건 달성 시 획득할 보상 아이템
    private PuppyItem puppyItem;

    // 컬렉션 이름: ex) 몸이 천근 만근
    private String collectionName;

    // 컬렉션 달성에 요구되는 특정 숙취의 횟수
    private Integer requiredNum;

    // 현재 숙취 횟수
    private Integer currentNum;

    // 달성 여부
    private boolean isCompleted;
}
