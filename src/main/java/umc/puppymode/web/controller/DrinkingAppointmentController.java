package umc.puppymode.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.apiPayload.code.status.SuccessStatus;
import umc.puppymode.domain.enums.AppointmentStatus;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentCommendService;
import umc.puppymode.service.DrinkingAppointmentService.DrinkingAppointmentQueryService;
import umc.puppymode.web.dto.DrinkingAppointmentRequestDTO;
import umc.puppymode.web.dto.DrinkingAppointmentResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class DrinkingAppointmentController {
    private final DrinkingAppointmentCommendService drinkingAppointmentCommendService;
    private final DrinkingAppointmentQueryService drinkingAppointmentQueryService;

    @PostMapping
    public ApiResponse<DrinkingAppointmentResponseDTO.AppointmentResultDTO> createAppointment(
            @Valid @RequestBody DrinkingAppointmentRequestDTO.AppointmentDTO request) {

        // 서비스 호출
        DrinkingAppointmentResponseDTO.AppointmentResultDTO response =
                drinkingAppointmentCommendService.createDrinkingAppointment(request);

        // 응답 반환
        return ApiResponse.onSuccess(response, SuccessStatus.APPOINTMENT_POST_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_POST_SUCCESS.getMessage());
    }

    @GetMapping
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
    public ApiResponse<DrinkingAppointmentResponseDTO.AppointmentResultDTO> getAppointmentById(
            @PathVariable Long appointmentId) {

        // 서비스 호출
        DrinkingAppointmentResponseDTO.AppointmentResultDTO response =
                drinkingAppointmentQueryService.getDrinkingAppointmentById(appointmentId);

        return ApiResponse.onSuccess(response, SuccessStatus.APPOINTMENT_GET_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_GET_SUCCESS.getMessage());
    }

    @DeleteMapping("/{appointmentId}")
    public ApiResponse<Void> deleteAppointment(@PathVariable Long appointmentId) {
        drinkingAppointmentCommendService.deleteDrinkingAppointment(appointmentId);
        return ApiResponse.onSuccess(SuccessStatus.APPOINTMENT_DELETE_SUCCESS.getCode(), SuccessStatus.APPOINTMENT_DELETE_SUCCESS.getMessage());
    }

}
