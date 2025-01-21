package umc.puppymode.service.DrinkingAppointmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import umc.puppymode.converter.DrinkingAppointmentConverter;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrinkingAppointmentQueryServiceImpl implements DrinkingAppointmentQueryService {

    private final DrinkingAppointmentRepository drinkingAppointmentRepository;

    @Override
    public DrinkingAppointmentResponseDTO.AppointmentResultDTO getDrinkingAppointmentById(Long appointmentId) {
        // 약속 조회
        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약속을 찾을 수 없습니다."));

        // 엔티티 → DTO 변환
        return DrinkingAppointmentConverter.toDTO(appointment);
    }

    @Override
    public DrinkingAppointmentResponseDTO.AppointmentListResultDTO getAllDrinkingAppointments(AppointmentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DrinkingAppointment> appointments;

        // 상태별 필터링
        if (status != null) {
            appointments = drinkingAppointmentRepository.findByStatus(status, pageable);
        } else {
            appointments = drinkingAppointmentRepository.findAll(pageable);
        }

        // DTO 변환
        List<DrinkingAppointmentResponseDTO.AppointmentSimpleDTO> appointmentDTOs = appointments.stream()
                .map(DrinkingAppointmentConverter::toSimpleDTO)
                .toList();

        return new DrinkingAppointmentResponseDTO.AppointmentListResultDTO(
                appointments.getTotalElements(),
                appointmentDTOs
        );
    }

    @Override
    public boolean isAppointmentActive(Long appointmentId) {

        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속을 찾을 수 없습니다."));

        // isActive 판별
        return appointment.getStatus() == AppointmentStatus.SCHEDULED;
    }
}
