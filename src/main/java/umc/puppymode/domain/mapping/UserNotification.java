package umc.puppymode.domain.mapping;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.NotificationCategory;
import umc.puppymode.domain.User;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class UserNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNotificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "notification_category_id")
    private NotificationCategory notificationCategory;
}
