package umc.puppymode.domain.mapping;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.User;
import umc.puppymode.domain.UserAlarmType;
import umc.puppymode.domain.common.BaseEntity;

@Entity
@Getter
@Setter
public class UserAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAlarmId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "alarm_type_id")
    private UserAlarmType alarmType;

    private Boolean isEnabled;
}
