package umc.puppymode.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.puppymode.apiPayload.ApiResponse;
import umc.puppymode.service.CalendarService.CalendarQueryService;
import umc.puppymode.service.AuthService.UserAuthService;
import umc.puppymode.web.dto.CalendarDTO.CalendarListResponseDTO;
import umc.puppymode.web.dto.CalendarDTO.CalendarResponseDTO.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {
    private final CalendarQueryService calendarQueryService;
    private final UserAuthService userAuthService;

    @GetMapping
    @Operation(summary = "캘린더 조회 API", description = "캘린더를 조회하는 API로, 사용자의 음주 기록 및 술약속 ID를 포함한 캘린더 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarListResponseDTO>>> getCalendar(@RequestParam String month) {
        Long userId = userAuthService.getCurrentUserId();
        List<CalendarListResponseDTO> responseDTO = calendarQueryService.getCalendar(userId, month);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    @GetMapping("/daily")
    @Operation(summary = "캘린더 상세 조회 API (음주 기록 조회)", description = "캘린더 상세를 조회하는 API로, 주어진 음주 기록 ID에 해당하는 음주 기록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarDetailDTO>>> getCalendarDetail(@RequestParam Long drinkHistoryId) {
        Long userId = userAuthService.getCurrentUserId();
        List<CalendarDetailDTO> responseDTO = calendarQueryService.getCalendarDetail(userId, drinkHistoryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
