package umc.puppymode.service.DrinkingAppointmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.converter.DrinkingAppointmentConverter;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.User;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.repository.UserRepository;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

@Service
@RequiredArgsConstructor
public class DrinkingAppointmentCommendServiceImpl implements DrinkingAppointmentCommendService {

    private final DrinkingAppointmentRepository repository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public DrinkingAppointmentResponseDTO.AppointmentResultDTO createDrinkingAppointment(
            DrinkingAppointmentRequestDTO.AppointmentDTO request) {

        // User 엔티티 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
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
    public void deleteDrinkingAppointment(Long appointmentId) {
        DrinkingAppointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약속을 찾을 수 없습니다."));

        repository.delete(appointment);
    }
}