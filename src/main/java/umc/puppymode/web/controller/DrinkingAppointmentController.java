package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.SuccessStatus;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentCommendService;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentQueryService;
import umc.puppymode.service.LocationService.LocationService;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;


@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class DrinkingAppointmentController {
    private final DrinkingAppointmentCommendService drinkingAppointmentCommendService;
    private final DrinkingAppointmentQueryService drinkingAppointmentQueryService;
    private final DrinkingAppointmentRepository drinkingAppointmentRepository;
    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "술 약속 생성 API", description = "술 약속 생성 API입니다 :)")
    public ApiResponse<DrinkingAppointmentResponseDTO.AppointmentResultDTO> createAppointment(
            @Valid @RequestBody DrinkingAppointmentRequestDTO.AppointmentDTO request) {

        // 서비스 호출
        DrinkingAppointmentResponseDTO.AppointmentResultDTO response =
                drinkingAppointmentCommendService.createDrinkingAppointment(request);

        // 응답 반환
        return ApiResponse.onSuccess(response, SuccessStatus.APPOINTMENT_POST_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_POST_SUCCESS.getMessage());
    }

    @GetMapping
    @Operation(summary = "술 약속 전체 조회 API", description = "술 약속 리스트를 전체 조회하는 API입니다. page는 0부터 시작합니다. :)")
    public ApiResponse<DrinkingAppointmentResponseDTO.AppointmentListResultDTO> getAllAppointments(
            @RequestParam(value = "status", required = false) AppointmentStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 서비스 호출
        DrinkingAppointmentResponseDTO.AppointmentListResultDTO response =
                drinkingAppointmentQueryService.getAllDrinkingAppointments(status, page, size);

        return ApiResponse.onSuccess(response, "SUCCESS_GET_ALL_APPOINTMENTS", "술 약속 리스트 조회 성공");
    }


    @GetMapping("/{appointmentId}")
    @Operation(summary = "술 약속 단일 조회 API", description = "술 약속 ID로 필터링하여 술 약속을 단일 조회하는 API입니다 :)")
    public ApiResponse<DrinkingAppointmentResponseDTO.AppointmentResultDTO> getAppointmentById(
            @PathVariable Long appointmentId) {

        // 서비스 호출
        DrinkingAppointmentResponseDTO.AppointmentResultDTO response =
                drinkingAppointmentQueryService.getDrinkingAppointmentById(appointmentId);

        return ApiResponse.onSuccess(response, SuccessStatus.APPOINTMENT_GET_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_GET_SUCCESS.getMessage());
    }

    @GetMapping("/{appointmentId}/status")
    @Operation(summary = "술 약속 및 음주 상태 조회하기  API", description = "술 약속 상태를 조회하며, 술 약속이 'ONGOING'일 때 음주 상태는 true로 반환합니다.:)")
    public ApiResponse<?> getAppointmentStatus(@PathVariable Long appointmentId) {

        // 음주 상태 가져오기
        boolean isDrinking = drinkingAppointmentQueryService.isDrinkingActive(appointmentId);

        // 업데이트된 상태를 엔티티에서 가져옴
        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalStateException("업데이트 후 약속을 찾을 수 없습니다."));

        DrinkingAppointmentResponseDTO.AppointmentStatusResultDTO response = new DrinkingAppointmentResponseDTO.AppointmentStatusResultDTO(appointmentId, isDrinking, appointment.getStatus());
        return ApiResponse.onSuccess(response, SuccessStatus.APPOINTMENT_STATUS_GET_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_STATUS_GET_SUCCESS.getMessage());
    }

    @DeleteMapping("/{appointmentId}")
    @Operation(summary = "술 약속 삭제 API", description = "술 약속을 삭제하는 API입니다 :)")
    public ApiResponse<Void> deleteAppointment(@PathVariable Long appointmentId) {

        drinkingAppointmentCommendService.deleteDrinkingAppointment(appointmentId);
        return ApiResponse.onSuccess(SuccessStatus.APPOINTMENT_DELETE_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_DELETE_SUCCESS.getMessage());
    }

    @PatchMapping("/{appointmentId}/reschedule")
    @Operation(summary = "술 약속 미루기 API", description = "술 약속 시간 재설정 후 데이터베이스에 patch합니다.")
    public ApiResponse<?> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody DrinkingAppointmentRequestDTO.RescheduleAppointmentRequestDTO request) {

        //시간 업데이트
        drinkingAppointmentCommendService.rescheduleDrinkingAppointment(appointmentId, request);

        // 업데이트된 상태를 엔티티에서 가져옴
        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalStateException("업데이트 후 약속을 찾을 수 없습니다."));

        DrinkingAppointmentResponseDTO.RescheduleResultDTO response = new DrinkingAppointmentResponseDTO.RescheduleResultDTO(appointmentId, appointment.getDateTime());

        return ApiResponse.onSuccess(SuccessStatus.APPOINTMENT_RESCHEDULED_PATCH_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_RESCHEDULED_PATCH_SUCCESS.getMessage());

    }

    @PatchMapping("/{appointmentId}/status")
    @Operation(summary = "음주 상태 종료하기", description = "술 약속 완료 후 음주 상태 종료")
    public ApiResponse<?> completedAppointmentStatus(
            @PathVariable Long appointmentId) {

        // 상태 업데이트
        drinkingAppointmentCommendService.completeDrinkingAppointment(appointmentId);

        // 업데이트된 상태를 엔티티에서 가져와서 "COMPLETED"로 전환된 시점에 updated_at 값을 가져오기
        DrinkingAppointment appointment = drinkingAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalStateException("업데이트 후 약속을 찾을 수 없습니다."));

        DrinkingAppointmentResponseDTO.CompletedResultDTO response = new DrinkingAppointmentResponseDTO.CompletedResultDTO(appointment.getUpdatedAt(), appointment.getStatus());

        return ApiResponse.onSuccess(response, "SUCCESS_END_DRINKING_APPOINTMENT", "음주 상태 종료 성공");
    }

    @PatchMapping("/{appointmentId}")
    @Operation(summary = "술 약속 시작하기 API", description = "사용자 현재 위치에 기반해 시간 + 장소가 범위 내에 들어오면 술 약속을 시작합니다.")
    public ResponseEntity<ApiResponse<DrinkingAppointmentResponseDTO.StartAppointmentResultDTO>> startAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody DrinkingAppointmentRequestDTO.StartAppointmentRequestDTO request) {

        // 사용자의 현 위치와 약속 위치 및 시간 확인 + 상태 업데이트
        ApiResponse<DrinkingAppointmentResponseDTO.StartAppointmentResultDTO> response = locationService.startAppointment(appointmentId, request);

        return ResponseEntity.ok(response);
    }

}
