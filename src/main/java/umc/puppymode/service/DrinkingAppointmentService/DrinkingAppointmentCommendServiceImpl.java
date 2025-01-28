package umc.puppymode.service.DrinkingAppointmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.converter.DrinkingAppointmentConverter;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DrinkingAppointmentCommendServiceImpl implements DrinkingAppointmentCommendService {

    private final DrinkingAppointmentRepository repository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public DrinkingAppointmentResponseDTO.AppointmentResultDTO createDrinkingAppointment(
            DrinkingAppointmentRequestDTO.AppointmentDTO request, Long userId) {

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID 입니다."));

        // 사용자가 요청한 날짜 확인
        LocalDate appointmentDate = request.getDateTime().toLocalDate();

        // 해당 날짜에 이미 약속이 있는지 확인
        boolean alreadyExists = repository.existsByUserAndDate(user, appointmentDate);

        if (alreadyExists) {
            throw new IllegalStateException(appointmentDate + "에 이미 약속이 생성되었습니다.");
        }

        // DTO → Entity 변환
        DrinkingAppointment entity = DrinkingAppointmentConverter.toEntity(request);
        // User 설정
        entity.setUser(user);
        // 데이터 저장
        DrinkingAppointment savedEntity = repository.save(entity);
        // Entity → DTO 변환
        return DrinkingAppointmentConverter.toDTO(savedEntity);
    }

    @Override
    @Transactional
    public void deleteDrinkingAppointment(Long appointmentId, Long userId) {

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID 입니다."));

        DrinkingAppointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약속을 찾을 수 없습니다."));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("해당 약속을 삭제할 권한이 없습니다.");
        }

        repository.delete(appointment);
    }

    @Override
    @Transactional
    public void rescheduleDrinkingAppointment(Long appointmentId, DrinkingAppointmentRequestDTO.RescheduleAppointmentRequestDTO request, Long userId) {

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID 입니다."));

        //약속 조회
        DrinkingAppointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약속을 찾을 수 없습니다."));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("해당 약속을 미룰 권한이 없습니다.");
        }

        // 시간 유효성 검증
        if (request.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("현재 시간 이전으로 설정할 수 없습니다.");
        }

        validateAppointmentStatusForRescheduling(appointment);

        appointment.setDateTime(request.getDateTime());
    }

    private static void validateAppointmentStatusForRescheduling(DrinkingAppointment appointment) {
        // 술 약속 상태 검증
        if (appointment.getStatus() == AppointmentStatus.ONGOING){
            throw new IllegalArgumentException("이미 진행 중인 약속은 미룰 수 없습니다.");
        } else if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("이미 완료 된 약속은 미를 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void completeDrinkingAppointment(Long appointmentId, Long userId) {

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID 입니다."));

        // 약속 조회
        DrinkingAppointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약속을 찾을 수 없습니다."));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("음주를 종료할 권한이 없습니다.");
        }

        // 해당 약속 상태가 ONGOING 상태 인지 확인
        AppointmentStatus status = appointment.getStatus();
        if (status == AppointmentStatus.ONGOING){
            appointment.setStatus(AppointmentStatus.COMPLETED);
        }
        else throw new IllegalArgumentException("현재 진행 중인 약속이 아닙니다.");
    }
}
