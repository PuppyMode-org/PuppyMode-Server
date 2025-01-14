package umc.puppymode.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String password;
    private Integer points;
    private Boolean receiveNotifications;
}

