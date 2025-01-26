package umc.puppymode.domain;


import jakarta.persistence.*;
import lombok.*;
import umc.puppymode.domain.common.BaseEntity;

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
    private String fcmToken;

    public void updatePoints(Integer points) {
        this.points += points;
    }
}

