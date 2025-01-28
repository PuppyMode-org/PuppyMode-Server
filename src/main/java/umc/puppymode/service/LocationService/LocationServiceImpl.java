package umc.puppymode.service.LocationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UrlPathHelper;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.ErrorStatus;
import umc.puppymode.apiPayload.exception.GeneralException;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentQueryService;
import umc.puppymode.util.DistanceCalculator;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final DrinkingAppointmentRepository drinkingAppointmentRepository;
    private final DistanceCalculator distanceCalculator;
    private final DrinkingAppointmentQueryService drinkingAppointmentQueryService;
    private final UrlPathHelper mvcUrlPathHelper;

    @Override
    public ApiResponse<DrinkingAppointmentResponseDTO.StartAppointmentResultDTO> startAppointment(Long appointmentId, DrinkingAppointmentRequestDTO.StartAppointmentRequestDTO request, Long userId) {
        // 약속 정보 조회
        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.APPOINTMENT_NOT_FOUND));

        // 약속 소유자 검증
        if (!appointment.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 약속을 시작할 권한이 없습니다.");
        }

        // 약속 장소 위도/경도
        double targetLatitude = appointment.getLatitude();
        double targetLongitude = appointment.getLongitude();

        // 사용자 현재 위치
        double userLatitude = request.getLatitude();
        double userLongitude = request.getLongitude();


        // 약속 장소 도착 확인 (1km 이내)
        boolean isWithinRange = isWithinArrivalRange(userLatitude, userLongitude, targetLatitude, targetLongitude);
        if (!isWithinRange) {
            return ApiResponse.onFailure("APPOINTMENT_OUT_OF_RANGE", "약속 장소와 너무 먼 거리입니다.", null);
        }

        // 약속 시간 확인 (유효 범위 내인지)
        ZonedDateTime appointmentTime = appointment.getDateTime().atZone(ZoneId.of("Asia/Seoul"));
        boolean isAppointmentTimeNow = isAppointmentTimeNow(appointmentTime);
        if (!isAppointmentTimeNow) {
            return ApiResponse.onFailure("APPOINTMENT_TIME_OUT_OF_RANGE", "약속 시간이 유효 범위를 벗어났습니다.", null);
        }

        // 조건 충족 시 약속 상태 업데이트
        appointment.setStatus(AppointmentStatus.ONGOING); // 상태를 "ONGOING"으로 변경
        drinkingAppointmentRepository.save(appointment);

        //음주 상태 조회
        boolean idDrinking = drinkingAppointmentQueryService.isDrinkingActive(appointmentId, userId);

        DrinkingAppointmentResponseDTO.StartAppointmentResultDTO response = new DrinkingAppointmentResponseDTO.StartAppointmentResultDTO(
                appointment.getAppointmentId(),
                appointment.getAddress(),
                appointment.getLocationName(),
                appointment.getStatus(),
                idDrinking,
                appointment.getUpdatedAt()
        );

        return ApiResponse.onSuccess(response, "SUCCESS_START_DRINKING_APPOINTMENT", "술 약속이 성공적으로 시작되었습니다.");
    }

    /**
     * 약속 장소 도착 범위 확인
     */
    private boolean isWithinArrivalRange(
            double userLatitude, double userLongitude, double targetLatitude, double targetLongitude
    ) {
        try {
            double distance = distanceCalculator.calculateDistance(
                    userLatitude, userLongitude, targetLatitude, targetLongitude
            );

            return distance <= 1.01; // 1km 이내 허용
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.DISTANCE_CALCULATION_FAILED);
        }
    }

    /**
     * 현재 시간이 약속 시간부터 +30분까지의 범위에 포함되는지, 약속 일 자정 이전인지, 확인
     */
    private boolean isAppointmentTimeNow(ZonedDateTime appointmentTime) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        //약속 종료 시점 -> 해당 약속 일 PM (23:59:59)
        ZonedDateTime endOfDay = appointmentTime.toLocalDate().atTime(23,59,59).atZone(ZoneId.of("Asia/Seoul"));
        // 약속 시간 + 30분 안에 들어오면..
        ZonedDateTime upperLimit = appointmentTime.plusMinutes(30);

        //제한 : 약속시간 +30분과 해당 약속일 자정 중 더 이른 값을 제한으로.. (다음 날로 넘어갈 경우 약속상태 전환 안됨)
        ZonedDateTime timeLimit = upperLimit.isBefore(endOfDay) ? upperLimit : endOfDay;

        // 현재 시간이 약속 시간과 같거나 이후이고, 상한값보다 이전인지 확인
        return !now.isBefore(appointmentTime) && now.isBefore(timeLimit);
    }
}


