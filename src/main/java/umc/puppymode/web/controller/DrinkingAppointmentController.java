package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.SuccessStatus;
import umc.puppymode.domain.DrinkingAppointment;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.repository.DrinkingAppointmentRepository;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentCommendService;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentQueryService;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class DrinkingAppointmentController {
    private final DrinkingAppointmentCommendService drinkingAppointmentCommendService;
    private final DrinkingAppointmentQueryService drinkingAppointmentQueryService;
    private final DrinkingAppointmentRepository drinkingAppointmentRepository;

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
    @Operation(summary = "술 약속 활성화 상태 조회 API", description = "술 약속 활성화 상태를 조회하며, 술 약속이 'SCHEDULED' 상태일 때 true로 반환합니다.:)")
    public ApiResponse<?> getAppointmentStatus(@PathVariable Long appointmentId) {

        boolean isActive = drinkingAppointmentQueryService.isAppointmentActive(appointmentId);
        DrinkingAppointmentResponseDTO.AppointmentStatusResultDTO response = new DrinkingAppointmentResponseDTO.AppointmentStatusResultDTO(appointmentId, isActive);
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

}
