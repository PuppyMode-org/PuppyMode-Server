package umc.puppymode.domain;

import umc.puppymode.domain.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HangoverItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hangoverId;

    private String hangoverName;

    private String imageUrl;
}
