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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrinkingAppointmentQueryServiceImpl implements DrinkingAppointmentQueryService {

    private final DrinkingAppointmentRepository drinkingAppointmentRepository;

    @Override
    public DrinkingAppointmentResponseDTO.AppointmentResultDTO getDrinkingAppointmentById(Long appointmentId, Long userId) {

        // 약속 조회
        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약속을 찾을 수 없습니다."));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 약속을 조회할 권한이 없습니다.");
        }

        return DrinkingAppointmentConverter.toDTO(appointment);
    }

    @Override
    public DrinkingAppointmentResponseDTO.AppointmentListResultDTO getAllDrinkingAppointments(AppointmentStatus status, int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DrinkingAppointment> appointments;

        // 상태와 사용자별 필터링
        if (status != null) {
            appointments = drinkingAppointmentRepository.findByUser_UserIdAndStatus(userId, status, pageable);
        } else {
            appointments = drinkingAppointmentRepository.findByUser_UserId(userId, pageable);
        }

        List<DrinkingAppointmentResponseDTO.AppointmentSimpleDTO> appointmentDTOs = appointments.stream()
                .map(DrinkingAppointmentConverter::toSimpleDTO)
                .toList();

        return new DrinkingAppointmentResponseDTO.AppointmentListResultDTO(
                appointments.getTotalElements(),
                appointmentDTOs
        );
    }

    @Override
    public boolean isDrinkingActive(Long appointmentId, Long userId) {

        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속을 찾을 수 없습니다."));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 약속에 접근할 권한이 없습니다.");
        }

        if (appointment.getStatus() == AppointmentStatus.ONGOING) {
            return true;
        }
        return false;
    }

    @Override
    public int getDrinkingDuration(Long appointmentId, Long userId) {

        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속을 찾을 수 없습니다."));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 약속에 접근할 권한이 없습니다.");
        }

        // 약속이 ONGOING 상태인지 확인
        if (appointment.getStatus() != AppointmentStatus.ONGOING) {
            return 0; // 약속이 진행 중이 아니면 0시간 반환
        }

        // 술 약속 시작 시간이 존재하는지 확인
        if (appointment.getDrinkingStartTime() == null) {
            return 0; // 시작 시간이 없으면 0시간 반환
        }

        // 술 약속 시작 시간 가져오기
        LocalDateTime drinkingStartTime = appointment.getDrinkingStartTime();

        // 현재 시간 가져오기
        LocalDateTime currentTime = LocalDateTime.now();

        // 두 시간의 차이 계산 (Duration 사용)
        Duration duration = Duration.between(drinkingStartTime, currentTime);
        long drinkingHours = duration.toHours();

        // 최소 1시간 이상 경과 시 반환, 1시간 미만이면 1시간으로 처리
        return Math.max(1, (int) drinkingHours);
    }

    @Override
    public Optional<DrinkingAppointmentResponseDTO.AppointmentSimpleDTO> getNearestScheduledAppointmentForToday(Long userId) {
        return drinkingAppointmentRepository.findNearestScheduledAppointmentForToday(userId)
                .map(DrinkingAppointmentConverter::toSimpleDTO);
    }
}
