package umc.puppymode.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAuthId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_auth_provider_user"))
    private User user;

    private String authProvider;

    private String authId;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
}