package umc.puppymode.domain.mapping;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.User;
import umc.puppymode.domain.UserPolicyType;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class UserPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPolicyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "policy_type_id")
    private UserPolicyType policyType;

    private Boolean isAgreed;
}

