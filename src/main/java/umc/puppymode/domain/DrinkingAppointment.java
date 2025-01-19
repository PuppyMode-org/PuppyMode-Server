package umc.puppymode.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import umc.puppymode.domain.common.BaseEntity;
import umc.puppymode.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class DrinkingAppointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId; // 술 약속 ID (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 술 약속 생성한 사용자 (FK)

    @Column(nullable = false)
    private LocalDateTime dateTime; // 술 약속 일시

    @Column(nullable = false, length = 255)
    private String locationName; // 장소 이름

    @Column(nullable = false)
    private Double latitude; // 장소 위도

    @Column(nullable = false)
    private Double longitude; // 장소 경도

    @Column(nullable = false, length = 500)
    private String address; // 장소 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status; // 상태 필드

}
