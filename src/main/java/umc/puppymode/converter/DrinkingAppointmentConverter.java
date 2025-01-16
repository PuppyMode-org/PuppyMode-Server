package umc.puppymode.converter;

import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;


public class DrinkingAppointmentConverter {
    // DTO → Entity 변환
    public static DrinkingAppointment toEntity(DrinkingAppointmentRequestDTO.AppointmentDTO dto) {
        DrinkingAppointment entity = new DrinkingAppointment();
        entity.setDateTime(dto.getDateTime());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setLocationName(dto.getLocationName());
        entity.setTitle(dto.getTitle());
        entity.setDetails(dto.getDetails());
        entity.setStatus(AppointmentStatus.SCHEDULED); // 기본값 설정
        return entity;
    }

    // Entity → DTO 변환, 상세 정보 위한 DTO -> 추후 약속 관련 상세 정보 포함 가능
    public static DrinkingAppointmentResponseDTO.AppointmentResultDTO toDTO(DrinkingAppointment entity) {
        return DrinkingAppointmentResponseDTO.AppointmentResultDTO.builder()
                .appointmentId(entity.getAppointmentId())
                .dateTime(entity.getDateTime())
                .address(entity.getAddress())
                .status(entity.getStatus().toString())
                .build();
    }

    // 간단한 정보 조회 위한 DTO
    public static DrinkingAppointmentResponseDTO.AppointmentSimpleDTO toSimpleDTO(DrinkingAppointment entity) {
        return DrinkingAppointmentResponseDTO.AppointmentSimpleDTO.builder()
                .appointmentId(entity.getAppointmentId())
                .dateTime(entity.getDateTime())
                .address(entity.getAddress())
                .status(entity.getStatus().name().toLowerCase()) //일단 API 명세에는 소문자로 되어있어서 소문자 처리.
                .build();
    }
}
