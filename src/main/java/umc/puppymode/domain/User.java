package umc.puppymode.domain;


import jakarta.persistence.*;
import lombok.*;
import umc.puppymode.domain.common.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String password;
    private Integer points;
    private Boolean receiveNotifications;

    public void updatePoints(Integer points) {
        this.points += points;
    }
}

